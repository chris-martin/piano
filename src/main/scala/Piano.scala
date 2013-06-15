import com.typesafe.config.Config

object Piano {

  def main(args: Array[String]) {

    import com.typesafe.config.ConfigFactory.{
      empty => emptyConfig,
      parseString => parseConfigString,
      load => systemConfig
    }

    val piano = new Piano()({
      val argsConfig = args.map(parseConfigString).foldLeft(emptyConfig)(_ withFallback _)
      argsConfig withFallback systemConfig.getConfig("piano")
    })

    piano.start()
  }

}

class Piano()(implicit config: Config) {

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
