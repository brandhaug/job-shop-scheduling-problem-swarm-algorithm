package swarm.ba

import jssp.{Job, Machine, OperationTimeSlot}
import swarm.SwarmUtils

/**
  * Bees Algorithm
  */
case class BA(jobs: Seq[Job], machines: Seq[Machine]) {
  val numberOfScouts: Int = 50

  val numberOfBestSites: Int = 5
  val recruitedBeesForBestSites: Int = 10
  val recruitedBeesForRemainingSites: Int = 3

  val maxTriesBeforeSiteAbandonment: Int = 5

  var globalBestBee: Bee = _

  def initializePopulation(): (Seq[Bee], Seq[OperationTimeSlot], Int) = {
    val scoutBees: Seq[Bee] = for (_ <- 0 to numberOfScouts) yield {
      val scoutBee = initializeScoutBee()

      if (globalBestBee == null || scoutBee.makeSpan < globalBestBee.makeSpan) {
        globalBestBee = scoutBee
      }

      scoutBee
    }

    (scoutBees, globalBestBee.schedule, globalBestBee.makeSpan)
  }

  def initializeScoutBee(): Bee = {
    val positionAndVelocity: Seq[(Int, Double, Double)] = SwarmUtils.initializePositionAndVelocity(jobs)
    val orderedPositionAndVelocity: Seq[(Int, Double, Double)] = positionAndVelocity.sortBy(_._2)
    val orderedPosition = orderedPositionAndVelocity.map(_._1)
    val schedule = SwarmUtils.decodePositionToSchedule(jobs, machines, orderedPosition)
    val makeSpan = SwarmUtils.calculateMakeSpan(schedule)
    val scoutBee: Bee = Bee(positionAndVelocity, makeSpan, schedule, 0)
    scoutBee
  }

  def initializeNeighborhood(scoutBee: Bee, neighborhoodSize: Int): Seq[Bee] = {
    val followerBees: Seq[Bee] = for (_ <- 0 to neighborhoodSize) yield {
      val positionAndVelocity: Seq[(Int, Double, Double)] = scoutBee.calculateNewPositionAndVelocity(scoutBee.positionAndVelocity)
      val orderedPositionAndVelocity: Seq[(Int, Double, Double)] = positionAndVelocity.sortBy(_._2)
      val orderedPosition = orderedPositionAndVelocity.map(_._1)
      val schedule = SwarmUtils.decodePositionToSchedule(jobs, machines, orderedPosition)
      val makeSpan = SwarmUtils.calculateMakeSpan(schedule)
      val followerBee: Bee = Bee(positionAndVelocity, makeSpan, schedule, 0)
      followerBee
    }

    followerBees
  }

  def tick(scoutBees: Seq[Bee]): (Seq[Bee], Seq[OperationTimeSlot], Int) = {
    val orderedScoutBees: Seq[Bee] = scoutBees.sortBy(_.makeSpan)

    val newScoutBees: Seq[Bee] = for ((scoutBee, i) <- orderedScoutBees.zipWithIndex) yield {
      val neighborhoodSize = if (i < numberOfBestSites) recruitedBeesForBestSites else recruitedBeesForRemainingSites
      val followerBees = initializeNeighborhood(scoutBee, neighborhoodSize)
      val orderedFollowerBees = followerBees.sortBy(_.makeSpan)
      val bestFollowerBee = orderedFollowerBees.head
      val newScoutBee = {
        if (bestFollowerBee.makeSpan < scoutBee.makeSpan) bestFollowerBee
        else if (scoutBee.tries + 1 > maxTriesBeforeSiteAbandonment) initializeScoutBee()
        else scoutBee.copy(tries = scoutBee.tries + 1)
      }

      if (newScoutBee.makeSpan < globalBestBee.makeSpan) {
        globalBestBee = scoutBee
      }

      newScoutBee
    }

    (newScoutBees, globalBestBee.schedule, globalBestBee.makeSpan)
  }
}