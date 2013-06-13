import scala.sys.process._

object HelloWorld {
  def main(args: Array[String]) {
    ConfigFactory.load()
    val instrumentDir = "/usr/share/zynaddsubfx/banks/"
    val instrument = instrumentDir + "SthPiano/0001-Soft Piano 1.xiz"
    Seq("padsp", "zynaddsubfx", "-U", "-L", instrument)
    Seq("jackd", "-d", "alsa")
    Seq("aconnect", "-i")
    Seq("aconnect", "-o")
    Seq("aconnect", "24", "128")
  }
}
