import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FuturePractice {
  def main(args: Array[String]): Unit = {
    //playFutureTraverse
    //playSequence
    //playFirstCompletedOf
    //playFind
    //playFold
    playReduce
  }

  import scala.concurrent.ExecutionContext.Implicits.global
  def playFutureTraverse = {
    //Futureを複数生成、実行
    val f: Future[List[Int]] = Future.traverse((1 to 12).toList) { i =>
      Future {
        println(s"start: $i")
        if (i == 5) Thread.sleep(5000) else Thread.sleep(1000)
        println(s"end: $i")
        i
      }
    }
    f.onSuccess{ case result: List[Int] => println(result.mkString(",")) }
    //これ付けないと終わらん
    Await.ready(f, Duration.Inf)
  }

  //traverseの簡易版らしい
  def playSequence = {
    val futures: List[Future[Int]] = (1 to 120).toList.map { i =>
      Future{
        println(i)
        Thread.sleep(1000)
        i
      }
    }

    val f:Future[List[Int]] = Future.sequence(futures)
    f.onSuccess{case result => println(result.toString)}
    //これ付けないと終わらん
    Await.ready(f, Duration.Inf)
  }

  def playFirstCompletedOf = {
    //並列実行するFutureの中で最初に終わったFutureを一つ返す
    val futures:Seq[Future[Int]] = Seq(
      Future{ Thread.sleep(3000); 1},
      Future{ Thread.sleep(2000); 2},
      Future{ Thread.sleep(1000); throw  new Exception("error in 3")},
      Future{ Thread.sleep(500); 4}
    )

    val f = Future.firstCompletedOf(futures)
    f.onSuccess{case result:Int => println("success "+ result)}
    f.onFailure{case t => println(t.getMessage())}

    Await.ready(f, Duration.Inf)
  }

  def playFind = {
    val futures :Seq[Future[Int]] = Seq(
      Future{ Thread.sleep(3000); 1},
      Future{ Thread.sleep(2000); 2},
      Future{ Thread.sleep(1000); 3},
      Future{ Thread.sleep(500); 4}
    )
    //条件を満たし、かつ一番早いFutureが取得できる
    val f = Future.find(futures){_%2 == 0}
    f.onSuccess{ case result: Option[Int] => println(result.get)}
    Await.ready(f, Duration.Inf)
  }

  def playFold = {
    val futures :Seq[Future[Int]] = Seq(
      Future{ Thread.sleep(3000); 1},
      Future{ Thread.sleep(2000); 2},
      Future{ Thread.sleep(1000); 3},
      Future{ Thread.sleep(500); 4}
    )
    //畳込み演算。初期値あるバージョン 10 + 4 + 3 + 2 + 1
    val f = Future.fold(futures)(10){(total, num) => total + num}
    f.onSuccess{case result: Int => println(result)}
    Await.ready(f, Duration.Inf)
  }

  def playReduce = {
     val futures :Seq[Future[Int]] = Seq(
      Future{ Thread.sleep(3000); 1},
      Future{ Thread.sleep(2000); 2},
      Future{ Thread.sleep(1000); 3},
      Future{ Thread.sleep(500); 4}
    )
    //畳込み演算。初期値ないバージョン 4 + 3 + 2 + 1
    val f = Future.reduce(futures){(total, num) => total + num}
    f.onSuccess{case result: Int => println(result)}
    Await.ready(f, Duration.Inf)
  }

}
