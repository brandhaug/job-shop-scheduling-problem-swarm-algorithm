package swarm.pso

import jssp.{JSSP, OperationTimeSlot}

import scala.util.Random

/**
  * Representation: Operation-Particle Position Sequence (OPPS)
  * The mapping between the particle and the scheduling solution is established
  * through connecting the operation sequence of all the jobs with the particle position sequence
  */
case class Particle(positionAndVelocity: Seq[(Int, Double, Double)],
                    makeSpan: Int,
                    schedule: Seq[OperationTimeSlot],
                    localBestPosition: Seq[Double],
                    localBestMakeSpan: Int,
                    localBestSchedule: Seq[OperationTimeSlot]) {

  def calculateNewPositionAndVelocity(positionAndVelocity: Seq[(Int, Double, Double)],
                                      inertiaWeight: Double,
                                      personalAccelerationConstant: Double,
                                      globalAccelerationConstant: Double,
                                      localBestPosition: Seq[Double],
                                      globalBestPosition: Seq[Double]): Seq[(Int, Double, Double)] = {

    // TODO: Ugly
    val jobIds = positionAndVelocity.map(_._1)
    val position = positionAndVelocity.map(_._2)
    val velocity = positionAndVelocity.map(_._3)

    val newVelocity: Seq[Double] = calculateNewVelocity(position,
                                                        velocity,
                                                        inertiaWeight,
                                                        personalAccelerationConstant,
                                                        globalAccelerationConstant,
                                                        localBestPosition,
                                                        globalBestPosition)

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
                           velocity: Seq[Double],
                           inertiaWeight: Double,
                           personalAccelerationConstant: Double,
                           globalAccelerationConstant: Double,
                           personalBestPosition: Seq[Double],
                           globalBestPosition: Seq[Double]): Seq[Double] = {


    val random: Random = Random
    val randomPersonalBestWeight = random.nextDouble()
    val randomGlobalBestWeight = random.nextDouble()

    val newVelocity: Seq[Double] = for ((operationVelocity, i) <- velocity.zipWithIndex) yield {
      val newOperationVelocity = (operationVelocity + inertiaWeight) +
        (personalAccelerationConstant * randomPersonalBestWeight * (personalBestPosition(i) - position(i))) +
        (globalAccelerationConstant * randomGlobalBestWeight * (globalBestPosition(i) - position(i)))

      if (newOperationVelocity < JSSP.minVelocity) JSSP.minVelocity
      else if (newOperationVelocity > JSSP.maxVelocity) JSSP.maxVelocity
      else newOperationVelocity
    }

    newVelocity
  }
}
