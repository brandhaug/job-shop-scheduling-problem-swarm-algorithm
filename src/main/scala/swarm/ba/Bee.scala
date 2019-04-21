package swarm.ba

import jssp.{JSSP, OperationTimeSlot}

import scala.util.Random

case class Bee(positionAndVelocity: Seq[(Int, Double, Double)],
               makeSpan: Int,
               schedule: Seq[OperationTimeSlot],
               tries: Int) {

  def calculateNewPositionAndVelocity(positionAndVelocity: Seq[(Int, Double, Double)]): Seq[(Int, Double, Double)] = {

    // TODO: Ugly
    val jobIds = positionAndVelocity.map(_._1)
    val position = positionAndVelocity.map(_._2)
    val velocity = positionAndVelocity.map(_._3)

    val newVelocity: Seq[Double] = calculateNewVelocity(position,
                                                        velocity)

    val newPosition: Seq[Double] = calculateNewPosition(position,
                                                        newVelocity)


    // TODO: Find a better way to merge Seq(a), Seq(b), Seq(c) to Seq(a, b, c)
    val newPositionAndVelocity = for (i <- jobIds.indices) yield {
      (jobIds(i), newPosition(i), newVelocity(i))
    }

    newPositionAndVelocity
  }

  def calculateNewPosition(position: Seq[Double], newVelocity: Seq[Double]): Seq[Double] = {
    // TODO: Find function to add each element with same index of two arrays into a new array
    val newPosition = for ((operationWeight, i) <- position.zipWithIndex) yield {
      operationWeight + newVelocity(i)
    }

    newPosition
  }

  def calculateNewVelocity(position: Seq[Double],
                           velocity: Seq[Double]): Seq[Double] = {


    val random: Random = Random
    val randomWeight = random.nextDouble()
    val randomWeight2 = random.nextDouble()

    val newVelocity: Seq[Double] = for ((operationVelocity, i) <- velocity.zipWithIndex) yield {
      val newOperationVelocity = operationVelocity + randomWeight - randomWeight2

      if (newOperationVelocity < JSSP.minVelocity) JSSP.minVelocity
      else if (newOperationVelocity > JSSP.maxVelocity) JSSP.maxVelocity
      else newOperationVelocity
    }

    newVelocity
  }
}
