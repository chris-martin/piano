package piano

import com.typesafe.{config => tsc}
import tsc.ConfigFactory
import ConfigFactory.{empty => emptyConfig, parseString => parseConfigString}

package object config {

  type Config = tsc.Config

  def argsConfig(args: Array[String]): Config =
    args.map(parseConfigString).foldLeft(emptyConfig)(_ withFallback _)

  def systemConfig = ConfigFactory.load

  def systemConfig(path: String): Config = systemConfig.getConfig(path)

}