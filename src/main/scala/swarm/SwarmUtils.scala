package swarm

import jssp.{JSSP, Job, Machine, Operation, OperationTimeSlot}

import scala.math.max
import scala.util.Random

object SwarmUtils {
  def initializePositionAndVelocity(jobs: Seq[Job]): Seq[(Int, Double, Double)] = {
    val random: Random = Random

    val positionAndVelocity: Seq[(Int, Double, Double)] = for (
      job: Job <- jobs;
      _: Operation <- job.operations
    ) yield {
      val operationWeight: Double = JSSP.minOperationWeight + (JSSP.maxOperationWeight - JSSP.minOperationWeight) * random.nextDouble()
      val velocity: Double = JSSP.minVelocity + (JSSP.maxVelocity - JSSP.minVelocity) * random.nextDouble()
      (job.id, operationWeight, velocity)
    }

    positionAndVelocity
  }

  def decodePositionToSchedule(jobs: Seq[Job], machines: Seq[Machine], position: Seq[Int]): Seq[OperationTimeSlot] = {
    var operationIndexes: Seq[Int] = jobs.map(_ => 0) // TODO

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

  def calculateNewPosition(position: Seq[Double], newVelocity: Seq[Double]): Seq[Double] = {
    position.zip(newVelocity).map { case (operationWeight, operationVelocity) => operationWeight + operationVelocity }
  }
}
