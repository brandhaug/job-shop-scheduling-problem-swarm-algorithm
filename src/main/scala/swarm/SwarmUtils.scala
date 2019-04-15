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

    // TODO: This is ugly, maybe separate the sequences before returning?
    positionAndVelocity
  }

  /**
    * @param position is a Seq with JobIds. Each Job contains a seq with Operations. The sequence contains duplicates of
    *                 jobs.
    *                 Ex: [0, 0, 1, 0, 1] means [jobs(0).operations(0), jobs(0).operations(1), jobs(1).operations(0), jobs(0).operations(2), jobs(1).operations(1)]
    * @return
    */
  def decodePositionToSchedule(jobs: Seq[Job], machines: Seq[Machine], position: Seq[Int]): Seq[OperationTimeSlot] = {
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
