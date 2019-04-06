package swarm.pso

import main.{Job, Machine, Operation}
import swarm.OperationTimeSlot

import scala.math.max
import scala.util.Random

/**
  * Particle Swarm Optimization
  * Based on Investigation of Particle Swarm Optimization for Job Shop Scheduling Problem
  * by Zhixiong Liu
  */
case class PSO(jobs: Seq[Job], machines: Seq[Machine]) {

  /**
    * Acceleration constants (c1 + c2 = 4)
    * Low values allow particles to roam far from target regions before being tugged back
    * High values result in abrupt movement towards, or past, target regions
    */
  val personalAccelerationConstant: Double = 2.0 // c1
  val globalAccelerationConstant: Double = 2.0 // c2

  val populationSize: Int = 50 // [10-50]

  val minOperationWeight: Double = 0.0
  val maxOperationWeight: Double = 2.0


  /**
    * Large inertia weight facilitates searching new area (0.9)
    * Small inertia weight facilitates fine-searching in the current search area (0.4)
    */
  var inertiaWeight: Double = 0.9
  val inertiaWeightDecay: Double = 0.995
  val minInertiaWeight = 0.4

  var globalBestParticle: Particle = _

  def initializePopulation(): Seq[Particle] = {
    val particles: Seq[Particle] = for (_ <- 0 to populationSize) yield {
      val positionAndVelocity: Seq[(Int, Double, Double)] = initializePositionAndVelocity(jobs)
      val orderedPositionAndVelocity: Seq[(Int, Double, Double)] = positionAndVelocity.sortBy(_._2)
      val orderedPosition = orderedPositionAndVelocity.map(_._1)
      val schedule = decodePositionToSchedule(orderedPosition)
      val makeSpan = calculateMakeSpan(schedule)
      val localBestPosition = positionAndVelocity.map(_._2)
      val particle = Particle(positionAndVelocity, makeSpan, localBestPosition, makeSpan)

      if (globalBestParticle == null || particle.makeSpan < globalBestParticle.makeSpan) {
        globalBestParticle = particle
      }

      particle
    }

    println("Global best make span: " + globalBestParticle.localBestMakeSpan)

    particles
  }

  def initializePositionAndVelocity(jobs: Seq[Job]): Seq[(Int, Double, Double)] = {
    val random: Random = Random

    val positionAndVelocity: Seq[(Int, Double, Double)] = for (
      job: Job <- jobs;
      _: Operation <- job.operations
    ) yield {
      val operationWeight: Double = minOperationWeight + (maxOperationWeight - minOperationWeight) * random.nextDouble()
      val velocity: Double = Particle.minVelocity + (Particle.maxVelocity - Particle.minVelocity) * random.nextDouble()
      (job.id, operationWeight, velocity)
    }

    positionAndVelocity
  }

  def tick(particles: Seq[Particle]): Seq[Particle] = {
    val newParticles: Seq[Particle] = for (particle: Particle <- particles) yield {
      val newPositionAndVelocity: Seq[(Int, Double, Double)] = particle.calculateNewPositionAndVelocity(particle.positionAndVelocity,
                                                                                                        inertiaWeight,
                                                                                                        personalAccelerationConstant,
                                                                                                        globalAccelerationConstant,
                                                                                                        particle.localBestPosition,
                                                                                                        globalBestParticle.localBestPosition)
      val orderedNewPositionAndVelocity: Seq[(Int, Double, Double)] = newPositionAndVelocity.sortBy(_._2)
      val orderedNewPosition = orderedNewPositionAndVelocity.map(_._1)
      val newSchedule = decodePositionToSchedule(orderedNewPosition)
      val newMakeSpan = calculateMakeSpan(newSchedule)

      // TODO: Merge to one if
      val newPosition = newPositionAndVelocity.map(_._2)
      val localBestPosition = if (newMakeSpan < particle.localBestMakeSpan) newPosition else particle.localBestPosition
      val localBestMakeSpan = if (newMakeSpan < particle.localBestMakeSpan) newMakeSpan else particle.localBestMakeSpan

      val newParticle = particle.copy(newPositionAndVelocity, newMakeSpan, localBestPosition, localBestMakeSpan)

      if (newParticle.makeSpan < globalBestParticle.makeSpan) {
        globalBestParticle = newParticle
      }

      newParticle
    }

    inertiaWeight = inertiaWeight * inertiaWeightDecay
    inertiaWeight = if (inertiaWeight < minInertiaWeight) minInertiaWeight else inertiaWeight

    println("Inertia weight: " + inertiaWeight)
    println("Global best make span: " + globalBestParticle.localBestMakeSpan)

    newParticles
  }

  /**
    * @param position is a Seq with JobIds. Each Job contains a seq with Operations. The sequence contains duplicates of
    *                 jobs.
    *                 Ex: [0, 0, 1, 0, 1] means [jobs(0).operations(0), jobs(0).operations(1), jobs(1).operations(0), jobs(0).operations(2), jobs(1).operations(1)]
    * @return
    */
  def decodePositionToSchedule(position: Seq[Int]): Seq[OperationTimeSlot] = {
    // Keeps track of which operation index of each job.
    // jobId = Seq index
    /// operationIndex = Seq value
    var operationIndexes: Seq[Int] = jobs.map(_ => 0) // TODO

    // The goal is to get all the operations in a seq
    val operationsOrdered: Seq[Operation] = for (jobId: Int <- position) yield {
      val operation: Operation = jobs(jobId).operations(operationIndexes(jobId))
      operationIndexes = operationIndexes.updated(jobId, operationIndexes(jobId).+(1))
      operation
    }

    var machineEndTimes: Seq[Int] = machines.map(_ => 0) // TODO
    var jobEndTimes: Seq[Int] = jobs.map(_ => 0) // TODO

    val schedule: Seq[OperationTimeSlot] = for (operation <- operationsOrdered) yield {
      val start: Int = max(machineEndTimes(operation.machineId), jobEndTimes(operation.jobId))
      val end: Int = start + operation.duration
      machineEndTimes = machineEndTimes.updated(operation.machineId, end)
      jobEndTimes = jobEndTimes.updated(operation.jobId, end)

      OperationTimeSlot(operation, start, end)
    }

    schedule
  }


  def calculateMakeSpan(schedule: Seq[OperationTimeSlot]): Int = {
    schedule.maxBy(_.end).end
  }
}
