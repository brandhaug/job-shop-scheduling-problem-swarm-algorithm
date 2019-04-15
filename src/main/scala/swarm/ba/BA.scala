package swarm.ba

import jssp.{Job, Machine, OperationTimeSlot}
import swarm.SwarmUtils

/**
  * Bees Algorithm
  */
case class BA(jobs: Seq[Job], machines: Seq[Machine]) {
  val numberOfBestSites: Int = 5
  val recruitedBeesForBestSites: Int = 10
  val recruitedBeesForRemainingSites: Int = 3

  val numberOfScouts: Int = 50
  val numberOfFollowers: Int = 0

  val initialPopulationSize: Int = 0
  val stagnationCycleForSiteAbandonmentLimit: Int = 0

  var globalBestBee: Bee = _

  def initializePopulation(): (Seq[Bee], Seq[OperationTimeSlot], Int) = {
    val scoutBees: Seq[Bee] = for (_ <- 0 to numberOfScouts) yield {
      val positionAndVelocity: Seq[(Int, Double, Double)] = SwarmUtils.initializePositionAndVelocity(jobs)
      val orderedPositionAndVelocity: Seq[(Int, Double, Double)] = positionAndVelocity.sortBy(_._2)
      val orderedPosition = orderedPositionAndVelocity.map(_._1)
      val schedule = SwarmUtils.decodePositionToSchedule(jobs, machines, orderedPosition)
      val makeSpan = SwarmUtils.calculateMakeSpan(schedule)
      val scoutBee: Bee = Bee(positionAndVelocity, makeSpan, schedule)

      if (globalBestBee == null || scoutBee.makeSpan < globalBestBee.makeSpan) {
        globalBestBee = scoutBee
      }

      scoutBee
    }

    (scoutBees, globalBestBee.schedule, globalBestBee.makeSpan)
  }

  def initializeNeighborhood(scoutBee: Bee, neighborhoodSize: Int): Seq[Bee] = {
    val followerBees: Seq[Bee] = for (_ <- 0 to neighborhoodSize) yield {
      val positionAndVelocity: Seq[(Int, Double, Double)] = scoutBee.calculateNewPositionAndVelocity(scoutBee.positionAndVelocity)
      val orderedPositionAndVelocity: Seq[(Int, Double, Double)] = positionAndVelocity.sortBy(_._2)
      val orderedPosition = orderedPositionAndVelocity.map(_._1)
      val schedule = SwarmUtils.decodePositionToSchedule(jobs, machines, orderedPosition)
      val makeSpan = SwarmUtils.calculateMakeSpan(schedule)
      val followerBee: Bee = Bee(positionAndVelocity, makeSpan, schedule)
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
      val newScoutBee = if (bestFollowerBee.makeSpan < scoutBee.makeSpan) bestFollowerBee else scoutBee

      if (newScoutBee.makeSpan < globalBestBee.makeSpan) {
        globalBestBee = scoutBee
      }

      newScoutBee
    }

    (newScoutBees, globalBestBee.schedule, globalBestBee.makeSpan)

    // Initialize population of numberOfScouts Scout Bees
    // Evaluate fitness

    // Neighborhood Search
    // Select numberOfBestSites for neighborhood search
    // Determine size of neighborhood
    // Recruit bees for Elite Sites (more bees for best sites)
    // Select fittest bee for each site

    // Assign (numberOfScoutBees - numberOfBestSites) remaining bees to random search


    // recruitment
    // for numberOfBestSites
    // localSearch (
    // siteAbondonment // not in standard ba
    // neighborhoodShrinking // not in standard ba


    // for i = numberOfBestSites,...,numberOfScoutBees
  }

  /**
    * Recruited followers are randomly scattered within the neighborhoods enclosing the solutions visited by the scouts
    * If any of the followers in a neighborhood lands on a solution of higher fitness than the solution visited by the scout, that followers becomes the new scout.
    */
  def localSearch(): Unit = {

  }

  /**
    * If no follower finds a solution of higher fitness, the size of the neighborhood is shrunk
    */
  def shrinkNeighborhood(): Unit = {

  }

  /**
    * If no improvement in fitness is recorded in a given neighborhood for a pre-set number of search cycles,
    * the local maximum of fitness is considered found,the patch is abandoned and a new scout is randomly generated
    */
  def siteAbandonment(): Unit = {

  }

  /**
    * Re-initialises the last (numberOfScouts - numberOfBestSites) neighborhoods with randomly generated solutions.
    */
  def globalSearch(): Unit = {

  }

  // Recruitment: scouts recruit followers to search further the neighborhoods of the most promising solutions
}
