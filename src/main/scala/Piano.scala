package piano

import config._
import filesystem._
import shutdown._

import scala.sys.process.ProcessLogger
import scala.util.{Success, Failure, Try}

object Piano {

  def main(args: Array[String]) {

    val config = argsConfig(args) withFallback defaultConfig
    val log = ProcessLogger(new File("piano.log"))

    val piano = new Piano(config, {f => println(f)}, log)

    addShutdownHook { piano.stop() }

    piano.start()
    log.flush()

    readLine()

    piano.stop()
    log.flush()
  }

  def defaultConfig = systemConfig("piano")

}

class Piano(
  config: Config = Piano.defaultConfig,
  console: (=> String) => Unit,
  log: ProcessLogger
) {

  import scala.sys.process._

  private var processes: Seq[Process] = Nil

  def start() {

    console("Starting jack ...")
    processes :+= Seq("jackd", "-d", "alsa").run(log)

    console("Starting zynaddsubfx ...")
    console(instrumentFile.getPath)
    processes :+= Seq("padsp", "zynaddsubfx", "-U", "-L", instrumentFile.getPath).run(log)

    console("Connecting devices ...")
    Thread.sleep(1000)
    val connected = Try {
      val keyboard = aconnect.inputs(log).get.filterAddressesByName(config.getString("keyboard.name")).head
      val synth = aconnect.outputs(log).get.filterAddressesByName("zynaddsubfx").head
      aconnect.connect(keyboard, synth, log)
    }

    connected match {
      case Success(_) =>
        console("Piano started.")
      case Failure(_) =>
        console("Piano failed to start.")
        stop()
    }

  }

  def stop() {
    processes.foreach(_.destroy())
    processes = Nil
  }

  private def instrumentFile: File = {

    val $ = config.getConfig("synth").getString(_)

    $("file") match {

      case filename if filename.nonEmpty =>
        new File(filename)

      case _ =>
        findFiles(new File($("directory")), $("name")).head
    }
  }

}
