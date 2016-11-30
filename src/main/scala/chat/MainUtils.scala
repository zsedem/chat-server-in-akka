/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import jline.console.ConsoleReader
import scala.annotation.tailrec
import scala.util.Try

object MainUtils {
  def activateJLine(help: String, prompt: String = "> ")(
      cb: PartialFunction[String, NextAction]): Printer = {
    val reader = ConsoleReader()
    reader.setPrompt(prompt)
    new Thread(() => {

      def readLine = Option(reader.readLine)

      @tailrec
      def loop(line: Option[String]): Unit =
        line.flatMap(cb.lift) match {

          case Some(Continue) =>
            loop(readLine)

          case None =>
            println(help)
            loop(readLine)

          case _ =>
        }

      Try(loop(readLine)).failed.foreach(_.printStackTrace())
    }).start()

    (str: String) =>
      {
        val buffer = reader.getCursorBuffer.buffer
        print(s"\r${" " * buffer.length}\r")
        println(str)
        print(prompt + buffer.toString)
      }
  }

  sealed trait NextAction
  case object Continue extends NextAction
  case object End extends NextAction

  private object ConsoleReader {
    def apply(): ConsoleReader = {
      val reader = new ConsoleReader(System.in, System.out)
      reader
    }
  }
}

trait Printer {
  def println(str: String): Unit
}
