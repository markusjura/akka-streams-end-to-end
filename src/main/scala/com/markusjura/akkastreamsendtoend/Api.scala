/*
 * Copyright 2018 Markus Jura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markusjura.akkastreamsendtoend

import akka.Done
import akka.actor.CoordinatedShutdown.{PhaseServiceUnbind, Reason}
import akka.actor.{CoordinatedShutdown, Scheduler, ActorSystem => UntypedSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.moia.streamee.Processor
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

/**
  * API for this demo.
  */
object Api extends Logging {

  final case class Config(address: String,
                          port: Int,
                          terminationDeadline: FiniteDuration,
                          ageProcessorTimeout: FiniteDuration)

  private final case class Request(question: String)

  private final object BindFailure extends Reason

  def apply(
      config: Config,
      ageProcessor: Processor[AgeCalculatorProcess.Request,
                              AgeCalculatorProcess.Response]
  )(implicit untypedSystem: UntypedSystem, mat: Materializer): Unit = {
    import Processor.processorUnavailableHandler
    import config._
    import untypedSystem.dispatcher

    implicit val scheduler: Scheduler = untypedSystem.scheduler

    Http()
      .bindAndHandle(
        route(ageProcessor, ageProcessorTimeout),
        address,
        port
      )
      .onComplete {
        case Failure(cause) =>
          logger.error(s"Shutting down, because cannot bind to $address:$port!",
            cause)
          CoordinatedShutdown(untypedSystem).run(BindFailure)

        case Success(binding) =>
          logger.info(
            s"Listening for HTTP connections on ${binding.localAddress}")
          CoordinatedShutdown(untypedSystem)
            .addTask(PhaseServiceUnbind, "api.unbind") { () =>
              binding.terminate(terminationDeadline).map(_ => Done)
            }
      }
  }

  def route(
      ageProcessor: Processor[AgeCalculatorProcess.Request,
                              AgeCalculatorProcess.Response],
      ageProcessorTimeout: FiniteDuration
  )(implicit ec: ExecutionContext, scheduler: Scheduler): Route = {
    import Directives._
    import ErrorAccumulatingCirceSupport._
    import io.circe.generic.auto._

    // format: off
    pathSingleSlash {
      get {
        complete(OK)
      } ~
        post {
          entity(as[Request]) {
            case Request(question) if question.isEmpty =>
              logger.warn("Bad Request. Question is empty.")
              complete(StatusCodes.BadRequest -> "Question is empty")

            case Request(question) =>
              onSuccess(
                ageProcessor.process(
                  AgeCalculatorProcess.Request(question), ageProcessorTimeout)) { response =>
                complete(StatusCodes.Created -> response.answer)
              }
          }
        }
    }
    // format: on
  }
}
