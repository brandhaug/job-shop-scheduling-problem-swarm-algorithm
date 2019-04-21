package swarm.ba

import jssp.{JSSP, OperationTimeSlot}
import swarm.SwarmUtils

import scala.util.Random

case class Bee(positionAndVelocity: Seq[(Int, Double, Double)], makeSpan: Int, schedule: Seq[OperationTimeSlot], tries: Int) {

  def calculateNewPositionAndVelocity(positionAndVelocity: Seq[(Int, Double, Double)]): Seq[(Int, Double, Double)] = {
    val (jobIds, position, velocity): (Seq[Int], Seq[Double], Seq[Double]) = positionAndVelocity.unzip3
    val newVelocity: Seq[Double]                                           = calculateNewVelocity(position, velocity)
    val newPosition: Seq[Double]                                           = SwarmUtils.calculateNewPosition(position, newVelocity)
    val newPositionAndVelocity                                             = jobIds.indices.map(i => (jobIds(i), newPosition(i), newVelocity(i)))

    newPositionAndVelocity
  }

  def calculateNewVelocity(position: Seq[Double], velocity: Seq[Double]): Seq[Double] = {
    val random: Random = Random
    val randomWeight   = random.nextDouble()
    val randomWeight2  = random.nextDouble()

    val newVelocity: Seq[Double] = for ((operationVelocity, i) <- velocity.zipWithIndex) yield {
      val newOperationVelocity = operationVelocity + randomWeight - randomWeight2

      if (newOperationVelocity < JSSP.minVelocity) JSSP.minVelocity
      else if (newOperationVelocity > JSSP.maxVelocity) JSSP.maxVelocity
      else newOperationVelocity
    }

    newVelocity
  }
}
