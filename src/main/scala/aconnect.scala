package aconnect {

  case class Client(id: Int, name: String, ports: Seq[Port])

  case class Port(id: Int, name: String)

}

package object aconnect {

  def parseString(s: String): Seq[Client] = {
    val result = Parser.parseAll(Parser.clients, s)
    result match {
      case Parser.NoSuccess(error, next) =>
        throw new RuntimeException(error + "\n" + next.pos)
      case _ => result.get
    }

  }

  private object Parser extends scala.util.parsing.combinator.RegexParsers {

    override def skipWhitespace = false

    lazy val clients: Parser[Seq[Client]] = rep(client <~ """\n*""".r)

    lazy val client: Parser[Client] =
      "client" ~ whiteSpace ~> id ~ (":" ~ whiteSpace ~> name <~ """[^\n]*\n""".r) ~ ports ^^
        { case id ~ name ~ ports => Client(id, name, ports) }

    lazy val ports: Parser[Seq[Port]] = rep(port)

    lazy val port: Parser[Port] =
      whiteSpace ~> id ~ (whiteSpace ~> name) <~ "\n" ^^
        { case id ~ name => Port(id, name) }

    lazy val id: Parser[Int] = """(\d+)""".r ^^ { _.toInt }

    lazy val name: Parser[String] =
      "'" ~> """((?:\\'|[^'])+)""".r <~ "'" ^^
        { _.trim.replace("""\'""", "'") }

  }

}
