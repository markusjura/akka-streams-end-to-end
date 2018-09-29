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

import akka.NotUsed
import akka.actor.Scheduler
import akka.pattern.after
import akka.stream.scaladsl.Flow
import java.util.UUID

import org.apache.logging.log4j.scala.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

object AgeCalculatorProcess extends Logging {

  final case class Request(question: String,
    correlationId: UUID = UUID.randomUUID()) {}
  final case class Response(answer: String,
    correlationId: UUID = UUID.randomUUID())

  /**
    * Simple domain logic process for demo purposes.
    *
    * The process is comprised of two stages (aka steps or tasks). Each of these performs its work
    * asynchronously and after an artificial delay, hence `mapAsync` is used. Typical real-world
    * examples for such stages are calls to external services (e.g. via HTTP or gRPC) or interacting
    * with actors in a request-response way (via the ask pattern).
    *
    * The value 1 for the `parallelism` of `mapAsync` is chosen for demonstration purposes only: It
    * allows for easily showing the effect of backpressure. For real-world applications usually a
    * higher value would be suitable.
    */
  def apply()(implicit ec: ExecutionContext,
    scheduler: Scheduler): Flow[Request, Response, NotUsed] =
    Flow[Request]
      .mapAsync(1) {
        case request @ Request(question, _) =>
          calculateAge(question)
            .map(age => request -> age)
      }
      .mapAsync(1) {
        case (request, age) =>
          wrapText(age)
            .map(text => Response(text, request.correlationId))
      }

  /**
    * Step that calculates the age based on the length of the question.
    */
  def calculateAge(question: String)(implicit ec: ExecutionContext,
    scheduler: Scheduler): Future[Int] =
    after(1.second, scheduler) {
      Future.successful(question.length)
    }

  /**
    * Step that wraps the age into a response text.
    */
  def wrapText(age: Int)(implicit ec: ExecutionContext,
    scheduler: Scheduler): Future[String] =
    after(1.second, scheduler) {
      Future.successful(s"I am $age years old")
    }
}
