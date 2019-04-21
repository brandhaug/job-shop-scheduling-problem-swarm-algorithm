package swarm.pso

import jssp.{JSSP, OperationTimeSlot}
import swarm.SwarmUtils

import scala.util.Random

case class Particle(positionAndVelocity: Seq[(Int, Double, Double)],
                    makeSpan: Int,
                    schedule: Seq[OperationTimeSlot],
                    localBestPosition: Seq[Double],
                    localBestMakeSpan: Int,
                    localBestSchedule: Seq[OperationTimeSlot]) {

  def calculateNewPositionAndVelocity(inertiaWeight: Double, globalBestPosition: Seq[Double]): Seq[(Int, Double, Double)] = {
    val (jobIds, position, velocity): (Seq[Int], Seq[Double], Seq[Double]) = positionAndVelocity.unzip3
    val newVelocity: Seq[Double]                                           = calculateNewVelocity(position, velocity, inertiaWeight, globalBestPosition)
    val newPosition: Seq[Double]                                           = SwarmUtils.calculateNewPosition(position, newVelocity)
    val newPositionAndVelocity                                             = jobIds.indices.map(i => (jobIds(i), newPosition(i), newVelocity(i)))

    newPositionAndVelocity
  }

  def calculateNewVelocity(position: Seq[Double], velocity: Seq[Double], inertiaWeight: Double, globalBestPosition: Seq[Double]): Seq[Double] = {
    val random: Random           = Random
    val randomPersonalBestWeight = random.nextDouble()
    val randomGlobalBestWeight   = random.nextDouble()

    val newVelocity: Seq[Double] = for ((operationVelocity, i) <- velocity.zipWithIndex) yield {
      val newOperationVelocity = (operationVelocity + inertiaWeight) +
        (Particle.personalAccelerationConstant * randomPersonalBestWeight * (localBestPosition(i) - position(i))) +
        (Particle.globalAccelerationConstant * randomGlobalBestWeight * (globalBestPosition(i) - position(i)))

      if (newOperationVelocity < JSSP.minVelocity) JSSP.minVelocity
      else if (newOperationVelocity > JSSP.maxVelocity) JSSP.maxVelocity
      else newOperationVelocity
    }

    newVelocity
  }
}

object Particle {

  /**
    * Acceleration constants (c1 + c2 = 4)
    * Low values allow particles to roam far from target regions before being tugged back
    * High values result in abrupt movement towards, or past, target regions
    */
  val personalAccelerationConstant: Double = 2.0 // c1
  val globalAccelerationConstant: Double   = 2.0 // c2
}
