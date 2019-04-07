package swarm.ba

import jssp.{Job, Machine, Operation, OperationTimeSlot}


/**
  * Bees Algorithm
  */
case class BA(jobs: Seq[Job], machines: Seq[Machine]) {
  val numberOfEliteSites: Int = 0
  val numberOfBestSites: Int = 0

  val recruitedBeesForBestSites: Int = 0
  val recruitedBeesForRemainingBestSites: Int = 0

  val numberOfScouts: Int = 20
  val numberOfFollowers: Int = 0

  val initialPopulationSize: Int = 0
  val stagnationCycleForSiteAbandonmentLimit: Int = 0


  def initializePopulation(): (Seq[Bee], Seq[OperationTimeSlot], Int) = {
    for (_ <- 0 to numberOfScouts) yield {
      val scoutBee: ScoutBee = ScoutBee()
      initializeNeighborhood(scoutBee)
    }

    val operation: Operation = Operation(0, 0, 0)
    (Seq(ScoutBee()), Seq(OperationTimeSlot(operation, 0, 0)), 0)
  }

  def initializeNeighborhood(scoutBee: ScoutBee): Unit = {

  }

  def tick(bees: Seq[Bee]): (Seq[Bee], Seq[OperationTimeSlot], Int) = {
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
    val operation: Operation = Operation(0, 0, 0)
    (Seq(ScoutBee()), Seq(OperationTimeSlot(operation, 0, 0)), 0)
  }

  /**
    * Recruited followers are randomly scattered within the neighborhoods enclosing the solutions visited by the scouts
    * If any of the followers in a neighborhood lands on a solution of higher fitness than the solution visited by the scout,that followers becomes the new scout.
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
