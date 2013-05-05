package util.redis

import org.sedis.Dress.Wrap
import com.typesafe.config.ConfigFactory
import java.io.File
import org.sedis.Pool
import redis.clients.jedis.{JedisPoolConfig, JedisPool}

object RedisClient {
  val config = ConfigFactory.load(ConfigFactory.parseFileAnySyntax(new File("conf/redis.conf")))
  val pool = new Pool(new JedisPool(new JedisPoolConfig(), config.getString("redis.host"), config.getInt("redis.port"), 2000))

  def withClient(f: Wrap => Unit): Unit = pool.withClient{ f }
}
