package piano

import config._

object Piano {

  def main(args: Array[String]) {
    val config = argsConfig(args) withFallback defaultConfig
    val piano = new Piano(config)
    piano.start()
  }

  def defaultConfig = systemConfig("piano")

}

class Piano(config: Config = Piano.defaultConfig) {

  import scala.sys.process._

  def instrumentPath: String = {
    val $ = config.getConfig("synth").getString(_)
    config getString $("file") match {
      case filename if filename.nonEmpty => filename
      case _ => ( Seq("find", $("directory"), "-iname", "*%s*" format $("name")) #| Seq("head", "-n", "1") ).!!
    }
  }

  Seq("padsp", "zynaddsubfx", "-U", "-L", instrumentPath)
  Seq("jackd", "-d", "alsa")
  Seq("aconnect", "-i")
  Seq("aconnect", "-o")
  Seq("aconnect", "24", "128")

  def start() {
  }

}
