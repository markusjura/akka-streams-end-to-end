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
import akka.actor.{Scheduler, ActorSystem => UntypedSystem}
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.Materializer
import akka.stream.typed.scaladsl.ActorMaterializer
import io.moia.streamee.Processor
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
import org.apache.logging.log4j.scala.Logging
import pureconfig.loadConfigOrThrow

import scala.concurrent.ExecutionContext

/**
  * Runner for this demo. Creates actor system, API, etc.
  */
object Main extends Logging {
  import akka.actor.typed.scaladsl.adapter._

  final case class Config(api: Api.Config)

  def main(args: Array[String]): Unit = {
    sys.props += "log4j2.contextSelector" -> classOf[AsyncLoggerContextSelector].getName // Always use async logging!

    val systemName = "akka-streams-end-to-end"
    val config = loadConfigOrThrow[Config](systemName) // Must be first!

    // TODO: Create the actor system
  }

  def apply(config: Config): Behavior[NotUsed] =
    // TODO: Implement Akka Typed startup logic
    ???
}
