package util.redis

import org.specs2.mutable.{After, Specification}
import com.typesafe.config.ConfigFactory
import java.io.File
import org.sedis._
import redis.clients.jedis._
import Dress._

class RedisClientSpec extends Specification {
  val config = ConfigFactory.load(ConfigFactory.parseFileAnySyntax(new File("conf/redis.conf")))
  val pool = new Pool(new JedisPool(new JedisPoolConfig(), config.getString("redis.host"), config.getInt("redis.port"), 2000))
  val testKey = "test:key"

  "RedisClient" should {
    "#withClient" >> {
      "redisにアクセスできる" >> DataCleaner {
        val value = "1"
        RedisClient.withClient{ client =>
          client.set(testKey, value)
        }
        pool.withClient{ client =>
          client.get(testKey) must beSome(value)
        }
      }
    }
  }

  object DataCleaner extends After {
    def after {
      pool.withClient { client =>
        client.del(testKey)
      }
    }
  }

}
