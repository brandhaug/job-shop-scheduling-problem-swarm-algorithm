//package swarm.ba
//
//import main.{Job, Machine}
//
///**
//  * Bees Algorithm
//  */
//case class BA(jobs: Seq[Job], machines: Seq[Machine]) {
//  val numberOfEliteSites: Int = 0
//  val numberOfBestSites: Int = 0
//
//  val recruitedBeesForBestSites: Int = 0
//  val recruitedBeesForRemainingBestSites: Int = 0
//
//  val numberOfScouts: Int = 0
//  val numberOfFollowers: Int = 0
//
//  val initialPopulationSize: Int = 0
//  val stagnationCycleForSiteAbandonmentLimit: Int = 0
//
//
//  def tick(): Unit {
//    // Initialize population of numberOfScouts Scout Bees
//    // Evaluate fitness
//
//    // Neighborhood Search
//    // Select numberOfBestSites for neighborhood search
//    // Determine size of neighborhood
//    // Recruit bees for Elite Sites (more bees for best sites)
//    // Select fittest bee for each site
//
//    // Assign (numberOfScoutBees - numberOfBestSites) remaining bees to random search
//
//
//    // recruitment
//    // for numberOfBestSites
//    // localSearch (
//    // siteAbondonment // not in standard ba
//    // neighborhoodShrinking // not in standard ba
//
//
//  // for i = numberOfBestSites,...,numberOfScoutBees
//
//  }
//
//  /**
//    * recruited followers are randomlys cattered within the flowerpatches enclosing the solutions visited by the scouts
//    * If any of the followers in a flowerpatch lands on a solution of higher fitness than the solution visited by the scout,that followers becomes the new scout.
//    */
//  def localSearch(): Unit {
//
//  }
//
//  /**
//    * If no follower finds a solution of higher fitness, the size of the flowerpatch is shrunk
//    * @return
//    */
//  def neighborhoodShrinking(): Unit {
//
//  }
//
//  /**
//    * If no improvement in fitness is recorded in a given flowerpatch for a pre-set number of search cycles,
//    * the local maximum of fitness is considered found,the patch is abandoned and a new scout is randomly generated
//    * @return
//    */
//  def siteAbandonment(): Unit {
//
//  }
//
//  /**
//    * re-initialises the last (numberOfScouts - numberOfBestSites) flowerpatches with randomly generated solutions.
//    * @return
//    */
//  def globalSearch(): Unit {
//
//  }
//
//  def initializePopulation(): Unit {
//    // For numberOfScouts
//    // initializeScout()
//    // initializeFlowerPatch() (a neighborhood is delimited)
//  }
//
//  // Recruitment: scouts recruit followers to search further the neighborhoods of the most promising solutions
//}
