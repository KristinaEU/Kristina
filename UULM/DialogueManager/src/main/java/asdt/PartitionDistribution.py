'''
A module for tracking a distribution over partitions of dialog states.  

A PartitionDistribution object maintains a distribution over 
many "Partition" objects.  The Partition class is implemented by 
the system developer.  The Partition class must implement a set 
of methods (defined below), which the PartitionDistribution object 
calls.

Part of the AT&T Statistical Dialog Toolkit (ASDT).  

Jason D. Williams
jdw@research.att.com
www.research.att.com/people/Williams_Jason_D
'''

import copy
from math import log
#import resource
import logging
from asdt.GlobalConfig import GetConfig
from owlSpeak.engine.his.interfaces import IPartitionDistribution
from owlSpeak.engine.his.interfaces import IHistory

MY_ID = 'PartitionDistribution'

class PartitionDistribution(IPartitionDistribution):
  '''
  Implements a distribution over partitions of dialog states.  
  
  Each partition corresponds to one partition of user goals, 
  and a set of dialog histories.
  
  Set configuration using the GlobalConfig module, provided
  with this distribution.
  
  Configuration parameters:
  
    [PartitionDistribution]
    defaultResetFraction: the default likelihood that the user's
    goal will revert to the prior.  This can be made system-action-
    specific by setting systemAction.resetFraction.  Default value 
    is 0.0.
    
    maxNBest: maximum number of NBest entries to consider (additional
    entries on the NBest list, if present, are ignored).  0 means 
    consider all NBest entries.  Default value is 0.
    
    maxPartitions: maximum number of partitions to maintain.  After 
    each NBest entry is incorporated, partitions are repeatedly 
    recombined until there are at most maxPartitions.  0 means not
    to attempt any recombination - allow number of partitions to grow
    without bound. Default value is 0.
    
    maxHistories: maximum number of histories to maintain for each
    partition.  At the end of each update, low probability histories
    are deleted until there are at most maxHistories histories for
    each partition.  0 means not to do any deleting - allow number of
    histories to grow without bound. Default value is 0.
  '''
  
  def __init__(self):
      self._topPartitionEntry = None
      self._secondTopPartitionEntry = None
      self._thirdTopPartitionEntry = None
      self._fourthTopPartitionEntry = None
      
#  def initialize(self, partitionSeedObject):
#      self.initialize(partitionSeedObject,None)
#  
  def initialize(self, partitionSeedObject, loading=False, historySeedObject=None):
    '''
    Creates a new partitionDistribution object.
    
    - partitionSeed: a function, which when called as 
      partitionSeed(), returns a list of root partitions.
    - historySeed: a function, which when called as 
      historySeed(partition), returns a list of starter 
      dialog histories.  If None, then no dialog history
      will be tracked.
    
    After creation, partitionDistributionObject.Init() method 
    is called.
    '''
    self.appLogger = logging.getLogger(MY_ID)
    self.maxNBest = 0
    self.maxPartitions = 100
    self.defaultResetFraction = 0.0
    self.maxHistories = 0
    self.useAggregateUserActionLikelihoods = False
    config = GetConfig()
    if (not config == None):
      if (config.has_option(MY_ID, 'defaultResetFraction')):
        self.defaultResetFraction = config.getfloat(MY_ID, 'defaultResetFraction')
      if (config.has_option(MY_ID, 'maxNBest')):
        self.maxNBest = config.getint(MY_ID, 'maxNBest') 
      if (config.has_option(MY_ID, 'maxPartitions')):
        self.maxPartitions = config.getint(MY_ID, 'maxPartitions')
      if (config.has_option(MY_ID, 'maxHistories')):
        self.maxHistories = config.getint(MY_ID, 'maxHistories')
      if (config.has_option(MY_ID, 'useAggregateUserActionLikelihoods')):
        self.useAggregateUserActionLikelihoods = config.getboolean(MY_ID, 'useAggregateUserActionLikelihoods')
    self.appLogger.info('Config: defaultResetFraction = %f' % (self.defaultResetFraction))
    self.appLogger.info('Config: maxNBest = %d' % (self.maxNBest))
    self.appLogger.info('Config: maxPartitions = %d' % (self.maxPartitions))
    self.appLogger.info('Config: maxHistories = %d' % (self.maxHistories))
    self.appLogger.info('Config: useAggregateUserActionLikelihoods = %s' % (self.useAggregateUserActionLikelihoods))
    self.partitionSeedObject = partitionSeedObject
    if (historySeedObject == None):
      self.historySeed = _DefaultHistory.Seed
      self.historySeedObject = None
    else:
      self.historySeed = None
      self.historySeedObject = historySeedObject
    self.stats = _Stats()
    if loading:
        self.load()
    else:
        self.Init()
    
  def load(self):
    '''
    Loads a partitionDistributionObject from an existing structure of partitions
      
    '''
      
#    print 'PartitionDistribution.load()'
    self.partitionEntryList = []
    self.nextPartitionEntryID = [0]  # use a list so that _PartitionEntry can modify this
    for partition in self.partitionSeedObject.partitionSeed():
#      print 'root:'
#      print partition.__str__()
      # consider each partition as a root partition
      rootEntry = _PartitionEntry(partition=partition, belief=partition.getBelief(), parent=None, nextPartitionEntryID=self.nextPartitionEntryID)
      
      if (self.historySeed != None):
          historyList = self.historySeed(partition)
          for history in historyList:
            historyEntry = _HistoryEntry()
            historyEntry.history = history
            historyEntry.belief = rootEntry.getBelief()
            historyEntry.origBelief = historyEntry.belief
            historyEntry.userActionLikelihoodTotal = 0.0
            rootEntry.historyEntryList.append(historyEntry)
        
      self.partitionEntryList.append(rootEntry)
      partitionList = []
      partitionList.append(rootEntry);
      
      # performing breadth-first search
      while len(partitionList) > 0:
          parent = partitionList.pop(0)
#          print 'parent:'
#          print parent.__str__()
          for child in parent.partition.getChildren():
#              print '--child:'
#              print child.__str__()
              childEntry = _PartitionEntry(partition=child, belief=child.getBelief(), parent=parent, nextPartitionEntryID=self.nextPartitionEntryID)
              if (self.historySeed != None):
                  historyList = self.historySeed(partition)
                  for history in historyList:
                    historyEntry = _HistoryEntry()
                    historyEntry.history = history
                    historyEntry.belief = childEntry.getBelief()
                    historyEntry.origBelief = historyEntry.belief
                    historyEntry.userActionLikelihoodTotal = 0.0
                    childEntry.historyEntryList.append(historyEntry)
              parent.children.append(childEntry)
              partitionList.append(childEntry)
              self.partitionEntryList.append(childEntry)
          
      
      
#      if (self.historySeed == None):
#        historyList = self.historySeedObject.historySeed(partition)
#      else:
#        historyList = self.historySeed(partition)
#      for history in historyList:
#        historyEntry = _HistoryEntry()
#        historyEntry.history = history
#        historyEntry.belief = history.prior
#        historyEntry.origBelief = historyEntry.belief
#        historyEntry.userActionLikelihoodTotal = 0.0
#        historyEntry.belief = history.prior
#        partitionEntry.historyEntryList.append(historyEntry)
      
      
  def Init(self):
    '''
    Initializes a partitionDistributionObject, for example 
    at the beginning of a dialog.
    
    Calls partitionSeed() to obtain a list of root partitions, 
    and for each calls historySeed(partition) to obtain a list 
    of starter dialog histories.  
    '''
    self.partitionEntryList = []
    self.nextPartitionEntryID = [0]  # use a list so that _PartitionEntry can modify this
    for partition in self.partitionSeedObject.partitionSeed():
      partitionEntry = _PartitionEntry(partition=partition, belief=partition.prior, parent=None, nextPartitionEntryID=self.nextPartitionEntryID)
      if (self.historySeed == None):
        historyList = self.historySeedObject.historySeed(partition)
      else:
        historyList = self.historySeed(partition)
      for history in historyList:
        historyEntry = _HistoryEntry()
        historyEntry.history = history
        historyEntry.belief = history.prior
        historyEntry.origBelief = historyEntry.belief
        historyEntry.userActionLikelihoodTotal = 0.0
        historyEntry.belief = history.prior
        partitionEntry.historyEntryList.append(historyEntry)
      self.partitionEntryList.append(partitionEntry)
      
  def Update(self, asrResult, sysAction = None):
    '''
    Updates a partitionDistributionObject object with 
    a system action and the resulting asrResult.
    '''
    #
    # Init 
    #
#    print 'PartitionDistribution.Update()'
    
    self._secondTopPartitionEntry = None
    self._topPartitionEntry = None
    self.stats.InitUpdate()
    
    self.stats.StartClock('mainUpdate')
    rawOnlistBeliefTotal = 0.0
    rawOfflistBeliefTotal = 0.0
    
    # Account for "resetFraction" -- the likelihood of the user's goal 
    # reverting to the original prior distribution
    try:
      resetFraction = sysAction.resetFraction
    except: 
      resetFraction = self.defaultResetFraction
    if (resetFraction > 0.0):
      self.appLogger.info("Applying resetFraction of %s" % (resetFraction))
      for partitionEntry in self.partitionEntryList:
        for historyEntry in partitionEntry.historyEntryList:
          if (historyEntry.belief > 0.0):
            historyFraction = historyEntry.belief / partitionEntry.belief
            historyEntry.belief = historyEntry.belief - resetFraction * (historyEntry.belief - historyFraction * partitionEntry.partition.prior)
            historyEntry.origBelief = historyEntry.belief
        partitionEntry.setBelief(partitionEntry.getBelief() - resetFraction * (partitionEntry.getBelief() - partitionEntry.partition.prior))
#        partitionEntry.UpdatePartition()
    
    # Initialize update
    # Add initial estimate of off-list probs
    self.appLogger.info('Start of update:')
    for (i, partitionEntry) in enumerate(self.partitionEntryList):
      partitionEntry.newBelief = 0.0
      for (j, historyEntry) in enumerate(partitionEntry.historyEntryList):
        partitionEntry.newBelief += historyEntry.belief
      rawOfflistBeliefTotal += partitionEntry.newBelief

    #
    # Main loop
    #
    
    # Iterate over items on the NBest list    
    partitionCount = len(self.partitionEntryList)
    for (n, (userAction, asrLikelihood, asrUnseenActionLikelihood)) in enumerate(asrResult):
      self.appLogger.info('working on userAction=%s (n=%d,asrProb=%f)' % (userAction, n, asrLikelihood))
      if (self.maxNBest > 0 and n >= self.maxNBest):
        break
      rawOfflistBeliefTotal = 0.0
      i = 0
      # 1. Grow tree through splitting 
      while (i < partitionCount):
        self.appLogger.info(' Trying to split partition %s ...' % (self.partitionEntryList[i].partition))
        
        # 1A. Do splitting (belief refinement)
        existingPartitionEntry = self.partitionEntryList[i]
#        print existingPartitionEntry.getBelief()
        self.stats.StartClock('split')
        newPartitions = existingPartitionEntry.partition.Split(userAction)
        self.stats.EndClock('split')
#        print 'length of new partitions: %d' % len(newPartitions)
        if (len(newPartitions) > 0):
          newBeliefFractionTotal = 0.0
          self.appLogger.info('  Split!  Parent (id %d) is now: %s' % (existingPartitionEntry.id, existingPartitionEntry.partition))
          for newPartition in newPartitions:
            self.stats.lastUpdateSplits += 1
            partitionCount += 1
            newPartitionEntry = _PartitionEntry(partition=newPartition, belief=None, parent=existingPartitionEntry, nextPartitionEntryID=self.nextPartitionEntryID)
#            print 'prior and belief'
#            print newPartitionEntry.partition.prior
#            print newPartitionEntry.getBelief()
            self.appLogger.info('   Created child id %d: %s' % (newPartitionEntry.id, newPartition))
#            print '   Created child id %d: %s' % (newPartitionEntry.id, newPartition)
            existingPartitionEntry.appendChild(newPartitionEntry)
#            existingPartitionEntry.UpdatePartition()
            if (newPartition.prior == 0.0 and existingPartitionEntry.partition.prior == 0.0):
              # The child has been split from a parent with zero belief
              newBeliefFraction = 0.0
            else:
              newBeliefFraction = newPartition.prior / (newPartition.prior + existingPartitionEntry.partition.prior)
#              print 'newPartition.prior: %f' % newPartition.prior
#              print 'divisor: %f' % (newPartition.prior + existingPartitionEntry.partition.prior)
              if (newBeliefFraction > 1.0):
                s = 'newBeliefFraction > 1.0\n'
                s += ' newBeliefFraction = %e\n' % (newBeliefFraction)
                s += ' newPartition.prior = %e\n' % (newPartition.prior)
                s += ' existingPartitionEntry.partition.prior = %e\n' % (existingPartitionEntry.partition.prior)
                raise RuntimeError, s
            newPartitionEntry.newBelief = existingPartitionEntry.newBelief * newBeliefFraction
            newBeliefFractionTotal += newBeliefFraction
            for existingHistoryEntry in existingPartitionEntry.historyEntryList:
              refinedExistingHistoryEntry = _HistoryEntry()
              refinedExistingHistoryEntry.history = existingHistoryEntry.history.Copy()
              refinedExistingHistoryEntry.belief = existingHistoryEntry.belief * newBeliefFraction
              refinedExistingHistoryEntry.origBelief = existingHistoryEntry.origBelief * newBeliefFraction
              refinedExistingHistoryEntry.userActionLikelihoodTotal = existingHistoryEntry.userActionLikelihoodTotal
              refinedExistingHistoryEntry.history = existingHistoryEntry.history.Copy()
              refinedExistingHistoryEntry.userActionLikelihoodTypes = existingHistoryEntry.userActionLikelihoodTypes.copy()
              newPartitionEntry.historyEntryList.append(refinedExistingHistoryEntry)
            for newHistoryEntry in existingPartitionEntry.newHistoryEntryList:
              refinedNewHistoryEntry = _HistoryEntry()
              refinedNewHistoryEntry.history = newHistoryEntry.history.Copy()
              refinedNewHistoryEntry.belief = newHistoryEntry.belief * newBeliefFraction
              refinedNewHistoryEntry.origBelief = None
              refinedNewHistoryEntry.userActionLikelihoodTotal = None
              refinedNewHistoryEntry.userActionLikelihoodTypes = None              
              newPartitionEntry.newHistoryEntryList.append(refinedNewHistoryEntry)
            self.partitionEntryList.append(newPartitionEntry)
          existingPartitionEntry.newBelief = existingPartitionEntry.newBelief * (1 - newBeliefFractionTotal)
          for existingHistoryEntry in existingPartitionEntry.historyEntryList:
            existingHistoryEntry.belief *= (1 - newBeliefFractionTotal)
            existingHistoryEntry.origBelief *= (1 - newBeliefFractionTotal)
          for newHistoryEntry in existingPartitionEntry.newHistoryEntryList:
            newHistoryEntry.belief = newHistoryEntry.belief * (1 - newBeliefFractionTotal)
        else:
          self.appLogger.info('  Could not split this partition.')
          
          
#        print '************************************************************'
#        for partitionEntry in self.partitionEntryList:
#          print 'belief'
#          print partitionEntry.getBelief()
#          print partitionEntry.partition.belief
#          print 'newBelief'
#          print partitionEntry.newBelief
#          print 'prior'
#          print partitionEntry.partition.prior
#          print '---'
#        print '************************************************************'
#      
        # 1B. Compute raw likelihoods and update offList mass
        self.appLogger.info(' Computing user action likelihoods for partition %s ...' % (self.partitionEntryList[i].partition))
        for existingHistoryEntry in existingPartitionEntry.historyEntryList:
          if (self.useAggregateUserActionLikelihoods):
            (userActionLikelihood, userActionLikelihoodType) = existingPartitionEntry.partition.UserActionLikelihood(userAction, existingHistoryEntry.history, sysAction)
            self.appLogger.info('  History = %s; userActionLikelihood=%0.15f; usereActionLikelihoodType=%s' % (existingHistoryEntry.history, userActionLikelihood, userActionLikelihoodType))
          else:
            userActionLikelihood = existingPartitionEntry.partition.UserActionLikelihood(userAction, existingHistoryEntry.history, sysAction)
            self.appLogger.info('  History = %s; userActionLikelihood=%0.15f' % (existingHistoryEntry.history, userActionLikelihood))
          if (userActionLikelihood > 0.0):
            # Update newHistoryEntry
            nextNewHistoryEntry = _HistoryEntry()
            nextNewHistoryEntry.history = existingHistoryEntry.history.Copy()
            nextNewHistoryEntry.belief = userActionLikelihood * asrLikelihood * existingHistoryEntry.origBelief
            nextNewHistoryEntry.origBelief = None
            nextNewHistoryEntry.userActionLikelihoodTotal = None
            nextNewHistoryEntry.history.Update(existingPartitionEntry.partition, userAction, sysAction)
            existingPartitionEntry.newHistoryEntryList.append(nextNewHistoryEntry)
            # Update existingHistoryEntry
            if (self.useAggregateUserActionLikelihoods):
              if (userActionLikelihoodType not in existingHistoryEntry.userActionLikelihoodTypes):
                # first time we've seen this type
                existingHistoryEntry.userActionLikelihoodTypes[userActionLikelihoodType] = userActionLikelihood
                newUserActionLikelihoodTotal = existingHistoryEntry.userActionLikelihoodTotal + userActionLikelihood
              else:
                # seen type before - check it's the same but dont add it to total
                if (not existingHistoryEntry.userActionLikelihoodTypes[userActionLikelihoodType] == userActionLikelihood):
                  s = 'Partition id = %d\n' % (existingPartitionEntry.id)
                  s += 'Partition = %s\n' % (existingPartitionEntry.partition.__str__())
                  s += 'History = %s\n' % (existingHistoryEntry.history.__str__())
                  s += 'UserActionLikelihoodTotal for this history in this partition = %e\n' % (existingHistoryEntry.userActionLikelihoodTotal)
                  s += 'UserAction = %s\n' % (userAction.__str__())
                  s += 'UserActionLikelihood = %e\n' % (userActionLikelihood)
                  s += 'UserActionLikelihoodType = %s\n' % (userActionLikelihoodType)
                  s += 'Before this UserActionLikelihoodType had value %e but now its different\n' % (existingHistoryEntry.userActionLikelihoodTypes[userActionLikelihoodType])
                  raise RuntimeError, s
                else:
                  newUserActionLikelihoodTotal = existingHistoryEntry.userActionLikelihoodTotal
            else:
              newUserActionLikelihoodTotal = existingHistoryEntry.userActionLikelihoodTotal + userActionLikelihood
            if (newUserActionLikelihoodTotal > 1.0):
              s = 'Partition id = %d\n' % (existingPartitionEntry.id)
              s += 'Partition = %s\n' % (existingPartitionEntry.partition.__str__())
              s += 'History = %s\n' % (existingHistoryEntry.history.__str__())
              s += 'UserActionLikelihoodTotal for this history in this partition = %e\n' % (existingHistoryEntry.userActionLikelihoodTotal)
              s += 'UserAction = %s\n' % (userAction.__str__())
              s += 'UserActionLikelihood = %e\n' % (userActionLikelihood)
              s += 'If I add this UserActionLikelihood to UserActionLikelihoodTotal for this history\n in this partition, it will exceed 1.0, which it cant\n'
              raise RuntimeError, s
            existingHistoryEntry.userActionLikelihoodTotal = newUserActionLikelihoodTotal
            existingPartitionEntry.newBelief = existingPartitionEntry.newBelief + nextNewHistoryEntry.belief
            rawOnlistBeliefTotal += nextNewHistoryEntry.belief
          else:
            pass
          offListUserActionLikelihood = 1.0 - existingHistoryEntry.userActionLikelihoodTotal
          # Re-compute the amount of mass for all offlist actions
          oldOffListHistoryEntryBelief = existingHistoryEntry.belief
          existingHistoryEntry.belief = existingHistoryEntry.origBelief * offListUserActionLikelihood * asrUnseenActionLikelihood
          existingPartitionEntry.newBelief = existingPartitionEntry.newBelief - oldOffListHistoryEntryBelief + existingHistoryEntry.belief
          rawOfflistBeliefTotal += existingHistoryEntry.belief
        self.appLogger.info('  Raw (unnormalized) log-belief in this partition is now %s' % (_LogToStringSafely(existingPartitionEntry.newBelief)))
        i += 1
        
    
#      print '************************************************************'
#      for partitionEntry in self.partitionEntryList:
#          print 'belief'
#          print partitionEntry.getBelief()
#          print partitionEntry.partition.belief
#          print 'newBelief'
#          print partitionEntry.newBelief
#          print 'prior'
#          print partitionEntry.partition.prior
#          print '---'
#      print '************************************************************'

      # 2. Compact tree by pruning
      if (partitionCount > self.stats.lastUpdateMaxPartitions):
        self.stats.lastUpdateMaxPartitions = partitionCount
      if (self.maxPartitions > 0 and partitionCount > self.maxPartitions):
        leafPartitionEntryList = []
        i = 0
        for partitionEntry in self.partitionEntryList:
          partitionEntry.selfPointer = i
          if (len(partitionEntry.children) == 0 and partitionEntry.parent != None):
            leafPartitionEntryList.append(partitionEntry)
          i += 1
        leafPartitionCount = len(leafPartitionEntryList)
        leafPartitionEntryList.sort(PartitionDistribution._ComparePartitionEntriesNewBelief)
        while (partitionCount > self.maxPartitions):
          for i in range(leafPartitionCount):
            partitionEntry = leafPartitionEntryList[i]
            # print '  Looking at partition %d...' % (partitionEntry.id)
            if (partitionEntry.parent.partition.Recombine(partitionEntry.partition)):
              self.appLogger.info('Combining child (id %d) into its parent (id %d)' % (partitionEntry.id, partitionEntry.parent.id))
              self.appLogger.info('Parent (%d) is now: %s' % (partitionEntry.parent.id, partitionEntry.parent.partition))
              parent = partitionEntry.parent
              # merge histories
              parent.historyEntryList.extend(partitionEntry.historyEntryList)
              PartitionDistribution._CombineHistoryDuplicatesOffList(parent.historyEntryList)
              parent.newHistoryEntryList.extend(partitionEntry.newHistoryEntryList)
              PartitionDistribution._CombineHistoryDuplicatesOnList(parent.newHistoryEntryList)
              # merge belief
              parent.newBelief = parent.newBelief + partitionEntry.newBelief
              # delete pointer to child
              parent.delChild( parent.children.index(partitionEntry) )
#              parent.UpdatePartition()
              self.partitionEntryList[ partitionEntry.selfPointer ] = None
              del leafPartitionEntryList[i]
              partitionCount -= 1
              leafPartitionCount -= 1
              # test if parent is now a leaf
              if (len(parent.children) == 0 and parent.parent != None):
                # find the right place to insert the parent
                insertedFlag = 0
                for j in range(leafPartitionCount):
                  if (parent.newBelief < leafPartitionEntryList[j].newBelief):
                    leafPartitionEntryList.insert(j, parent)
                    insertedFlag = 1
                    break
                if (insertedFlag == 0):
                  leafPartitionEntryList.append(parent)
                leafPartitionCount += 1
              break
        # Clean up empty entries
        cleanPartitionEntryList = []
        for partitionEntry in self.partitionEntryList:
          if (partitionEntry != None):
            cleanPartitionEntryList.append(partitionEntry)
        self.partitionEntryList = cleanPartitionEntryList

    rawBeliefTotal = rawOfflistBeliefTotal + rawOnlistBeliefTotal
    
#    print '************************************************************'
#    for partitionEntry in self.partitionEntryList:
#          print 'belief'
#          print partitionEntry.getBelief()
#          print partitionEntry.partition.belief
#          print 'newBelief'
#          print partitionEntry.newBelief
#          print 'prior'
#          print partitionEntry.partition.prior
#          print '---'
#    print '************************************************************'
    
    # 3. Normalize, clean up and sort
    if (rawBeliefTotal == 0.0):
      self.stats.EndClock('mainUpdate')
      return
    rawOfflistBeliefTotalCheck = 0.0
    rawOnlistBeliefTotalCheck = 0.0
    i = 0
    for partitionEntry in self.partitionEntryList:
      for newHistoryEntry in partitionEntry.newHistoryEntryList:
        rawOnlistBeliefTotalCheck += newHistoryEntry.belief
      for historyEntry in partitionEntry.historyEntryList:
        historyEntry.history.Update(partitionEntry.partition, None, sysAction)
        partitionEntry.newHistoryEntryList.append(historyEntry)
        rawOfflistBeliefTotalCheck += historyEntry.belief
      partitionEntry.historyEntryList = partitionEntry.newHistoryEntryList
      partitionEntry.newHistoryEntryList = []
      PartitionDistribution._CombineHistoryDuplicatesOnList(partitionEntry.historyEntryList)
      partitionEntry.setBelief(0.0)
      partitionEntry.newBelief = 0.0
      for historyEntry in partitionEntry.historyEntryList:
        historyEntry.belief = historyEntry.belief / rawBeliefTotal
        historyEntry.origBelief = historyEntry.belief
        historyEntry.userActionLikelihoodTotal = 0.0
        historyEntry.userActionLikelihoodTypes = {}
        if (historyEntry.belief < 0.0):
          s = 'historyEntry.belief < 0: %e\n' % (historyEntry.belief)
          s += ' rawBeliefTotal = %e' % (rawBeliefTotal)
          raise RuntimeError, s
        partitionEntry.setBelief(partitionEntry.getBelief() + historyEntry.belief)
#      partitionEntry.UpdatePartition()
      partitionEntry.historyEntryList.sort(PartitionDistribution._CompareHistoryEntries)
      if (self.maxHistories > 0 and len(partitionEntry.historyEntryList) > self.maxHistories):
        deletedMass = 0.0
        while (len(partitionEntry.historyEntryList) > self.maxHistories):
          deletedMass += partitionEntry.historyEntryList[0].belief
          del partitionEntry.historyEntryList[0]
        increaseFactor = 1.0 / (1.0 - deletedMass)
        for historyEntry in partitionEntry.historyEntryList:
          historyEntry.belief *= increaseFactor
          historyEntry.origBelief = historyEntry.belief
      i += 1
    self.partitionEntryList.sort(PartitionDistribution._ComparePartitionEntries)
    self.stats.EndClock('mainUpdate')
    
#    for partitionEntry in self.partitionEntryList:
#        partitionEntry.UpdatePartition()
    
#    print '************************************************************'
#    for partitionEntry in self.partitionEntryList:
#          print 'belief'
#          print partitionEntry.getBelief()
#          print partitionEntry.partition.belief
#          print 'newBelief'
#          print partitionEntry.newBelief
#          print 'prior'
#          print partitionEntry.partition.prior
#          print '---'
#    print '************************************************************'

  def Compact(self, maxPartitions):
    '''
    Compacts a partitionDistribution object down 
    to at most maxPartitions.
    
    This function does not need to be called as
    a part of the normal operation of the class.  
    '''
      
#    print 'PartitionDistribution.Compact()'
    self.stats.InitUpdate()
    self.stats.StartClock('compact')
    assert maxPartitions > 0, 'maxPartitions must be > 0'
    partitionCount = len(self.partitionEntryList)
    self.stats.lastUpdateMaxPartitions = partitionCount
    if (maxPartitions > 0 and partitionCount > maxPartitions):
      leafPartitionEntryList = []
      i = 0
      for partitionEntry in self.partitionEntryList:
        partitionEntry.selfPointer = i
        if (len(partitionEntry.children) == 0 and partitionEntry.parent != None):
          leafPartitionEntryList.append(partitionEntry)
        i += 1
      leafPartitionCount = len(leafPartitionEntryList)
      leafPartitionEntryList.sort(PartitionDistribution._ComparePartitionEntries)
      while (partitionCount > maxPartitions):
        for i in range(leafPartitionCount):
          partitionEntry = leafPartitionEntryList[i]
          if (partitionEntry.parent.partition.Recombine(partitionEntry.partition)):
            self.appLogger.info('Combining child (id %d) into its parent (id %d)' % (partitionEntry.id, partitionEntry.parent.id))
            self.appLogger.info('Parent (%d) is now: %s' % (partitionEntry.parent.id, partitionEntry.parent.partition))
            parent = partitionEntry.parent
            # merge histories
            parent.historyEntryList.extend(partitionEntry.historyEntryList)
            PartitionDistribution._CombineHistoryDuplicatesOnList(parent.historyEntryList)
            # merge belief
            parent.setBelief(parent.getBelief() + partitionEntry.getBelief())
            # delete pointer to child
            parent.delChild( parent.children.index(partitionEntry) )
            self.partitionEntryList[ partitionEntry.selfPointer ] = None
            del leafPartitionEntryList[i]
            partitionCount -= 1
            leafPartitionCount -= 1
            # test if parent is now a leaf
            if (len(parent.children) == 0 and parent.parent != None):
              # find the right place to insert the parent
              insertedFlag = 0
              for j in range(leafPartitionCount):
                if (parent.getBelief() < leafPartitionEntryList[j].getBelief()):
                  leafPartitionEntryList.insert(j, parent)
                  insertedFlag = 1
                  break
              if (insertedFlag == 0):
                leafPartitionEntryList.append(parent)
              leafPartitionCount += 1
            break
      # Clean up empty entries
      cleanPartitionEntryList = []
      for partitionEntry in self.partitionEntryList:
        if (partitionEntry != None):
          cleanPartitionEntryList.append(partitionEntry)
      self.partitionEntryList = cleanPartitionEntryList
      for partitionEntry in self.partitionEntryList:
        partitionEntry.historyEntryList.sort(PartitionDistribution._CompareHistoryEntries)
      self.partitionEntryList.sort(PartitionDistribution._ComparePartitionEntries)
    self.stats.EndClock('compact')

  @staticmethod
  def _ComparePartitionEntries(a, b):
    return cmp(a.getBelief(), b.getBelief())
  
  @staticmethod
  def _CompareHistoryEntries(a, b):
    return cmp(a.belief, b.belief)
  
  @staticmethod
  def _ComparePartitionEntriesNewBelief(a, b):
    return cmp(a.newBelief, b.newBelief)
  
  @staticmethod
  def _CombineHistoryDuplicatesOnList(historyEntryList):
    i = 0
    length = len(historyEntryList)
    while (i < length):
      j = i + 1
      while (j < length):
        if (historyEntryList[i].history == historyEntryList[j].history):
          historyEntryList[i].belief += historyEntryList[j].belief
          del historyEntryList[j]
          length -= 1
        else:
          j = j + 1
      i = i + 1
    
  @staticmethod
  def _CombineHistoryDuplicatesOffList(historyEntryList):
    i = 0
    length = len(historyEntryList)
    while (i < length):
      j = i + 1
      while (j < length):
        if (historyEntryList[i].history == historyEntryList[j].history):
          historyEntryList[i].belief += historyEntryList[j].belief
          historyEntryList[i].origBelief += historyEntryList[j].origBelief
          del historyEntryList[j]
          length -= 1
        else:
          j = j + 1
      i = i + 1
      
  def toString(self):
    return self.__str__()

  def getLoggingContent(self):
    return self.appLogger.__str__()

  def __str__(self):
    resultList = ['( id,pid) belief  logBel  [logPri ] description']
    for partitionEntry in self.partitionEntryList:
#      print partitionEntry.getBelief()
#      print partitionEntry.partition.belief
#      print partitionEntry.partition.prior
      logProb = '      -' if (partitionEntry.getBelief() == 0) else '%7.3f' % (log(partitionEntry.getBelief()))
      priorLogProb = '      -' if (partitionEntry.partition.prior == 0) else '%7.3f' % (log(partitionEntry.partition.prior))
      parentID = '-' if (partitionEntry.parent == None) else partitionEntry.parent.id
      s = '(%3.d,%3s) %.5f %5s [%5s] %s' % (partitionEntry.id, parentID, partitionEntry.getBelief(), logProb, priorLogProb, partitionEntry.partition.__str__())
      resultList.append(s)
      for historyEntry in partitionEntry.historyEntryList:
        logProb = '      -' if (historyEntry.belief == 0) else '%7.3f' % (log(historyEntry.belief))
        s = '          %.5f %5s           %s' % (historyEntry.belief, logProb, historyEntry.history.__str__())
        resultList.append(s)
    return '\n'.join(resultList)

  def getTopPartition(self):
    if self._topPartitionEntry == None:
        self._computeTopPartitions()
    
    if self._topPartitionEntry != None:
        return self._topPartitionEntry.partition
    else:
        return None
    
  def getTopPartitionsHistory(self):
    if self._topPartitionEntry == None:
        self._computeTopPartitions()
        
    topHistoryEntry = None
    topHistoryBelief = -1
    for historyEntry in self._topPartitionEntry.historyEntryList:
        if historyEntry.belief > topHistoryBelief:
            topHistoryBelief = historyEntry.belief
            topHistoryEntry = historyEntry
            
    if topHistoryEntry != None:
        return topHistoryEntry.history
    else:
        return None
    
  def getSecondTopPartition(self):
    if self._secondTopPartitionEntry == None:
        self._computeTopPartitions()
    
    if self._secondTopPartitionEntry != None:
        return self._secondTopPartitionEntry.partition
    else:
        return None
  
  def _computeTopPartitions(self):
        belief = -1.0
        maxPartitionEntry = None
        belief2 = -1.0
        maxPartitionEntry2 = None
        
        for partitionEntry in self.partitionEntryList:
            if partitionEntry.getBelief() > belief2:
                if partitionEntry.getBelief() >= belief:
                    belief2 = belief
                    maxPartitionEntry2 = maxPartitionEntry
                    belief = partitionEntry.getBelief()
                    maxPartitionEntry = partitionEntry
                else:
                    belief2 = partitionEntry.getBelief()
                    maxPartitionEntry2 = partitionEntry
        if maxPartitionEntry != None:
            self._topPartitionEntry = maxPartitionEntry
            if maxPartitionEntry2 != None:
                self._secondTopPartitionEntry = maxPartitionEntry2
            else:
                self._secondTopPartitionEntry = None
        else:
            self._topPartitionEntry = None
            self._secondTopPartitionEntry = None


def _LogToStringSafely(n):
#    print 'n %f' % n
    if (n == 0.0):
        return '-'
    elif (n < 0.0):
        raise RuntimeError,'Cannot take log of value less than 0: %f' % (n)
    else:
        return '%s' % (log(n))
    
class _PartitionEntry(object):
  __slots__ = ['children', 'historyEntryList','newHistoryEntryList','id','partition','parent','belief','selfPointer','newBelief'] 
  def __init__(self,partition,belief,parent,nextPartitionEntryID):
#    print "_PartitionEntry.__init__()"
    self.children = []
    self.historyEntryList = []
    self.newHistoryEntryList = []
    self.id = nextPartitionEntryID[0]
    nextPartitionEntryID[0] += 1
    self.partition = partition
    self.setBelief(belief)
#    self.belief = belief
    self.setParent(parent)
    self.parent = parent
    self.selfPointer = -1
    self.newBelief = None
#    self.UpdatePartition()
    
  def setBelief(self,belief):
#      self.belief = belief
    if belief == None:
        self.partition.setBelief(-1.0)
    else:
        self.partition.setBelief(belief)
      
  def getBelief(self):
#      return self.belief
      return self.partition.getBelief()
  
  def setParent(self,parent):
      self.parent = parent
      if parent != None:
          self.partition.setParent(parent.partition)
      else:
          self.partition.setParent(None)
          
  def appendChild(self,child):
      self.children.append(child)
      self.partition.addChild(child.partition)
      
  def delChild(self,childIdx):
      self.partition.removeChild(self.children[ childIdx ].partition)
      del self.children[ childIdx ]
      
#  def getParent(self):
#      return self.parent
##      return partition.getParent()
  
#  def UpdatePartition(self):
##    print "_PartitionEntry.UpdatePartition()"
##    if self.belief != None:
##        self.partition.setBelief(self.belief)
#    
##    if self.parent != None:
##        self.partition.setParent(self.parent.partition)
#
##    chldrn = []
##    for child in self.children:
##        chldrn.append(child.partition)
##    self.partition.setChildren(chldrn)
#    pass
    

        
class _HistoryEntry(object):
  __slots__ = ['belief','history','userActionLikelihoodTotal','origBelief','userActionLikelihoodTypes']
  def __init__(self):
      self.userActionLikelihoodTypes = {}
    
class _Stats(object):
  def __init__(self):
    self.InitUpdate()
    
  def InitUpdate(self):
    self.lastUpdateMaxPartitions = 0
    self.lastUpdateSplits = 0
    self.clocks = {}
    self.clocksTemp = {}
    
  def StartClock(self,name):
    self.clocksTemp[name] = self._CPU()

  def EndClock(self,name):
    if (name in self.clocks):
        self.clocks[name] += self._CPU() - self.clocksTemp[name]
    else:
        self.clocks[name] = self._CPU() - self.clocksTemp[name]

  @staticmethod
  def _CPU():
    return 0
#    return (resource.getrusage(resource.RUSAGE_SELF).ru_utime+
#            resource.getrusage(resource.RUSAGE_SELF).ru_stime)

class _DefaultHistory(IHistory):
  @staticmethod
  def Seed(partition):
    return [_DefaultHistory()]
    
  def __init__(self):
    self.prior = 1.0

  def __eq__(self,otherHistory):
    return True

  def Update(self,partition,userAction,sysAction):
    return

  def Copy(self):
    return _DefaultHistory()
    
  def __str__(self):
    return '-'


    
      
