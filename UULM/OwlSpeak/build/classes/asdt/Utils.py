from owlSpeak.engine.his.interfaces import INBestList

class NBestList(INBestList):
  '''
  Implements an NBest list ASR result.  The NBest list is simply a list of UserActions, each
  with an associated probability of correctness.  
  '''
  def __init__(self):
      pass
  
  def initialize(self,userActions,probs,numActions):
    '''
    userActions is a list of user actions; probs is a list of their probabilities of correctness
    '''
    self.userActions = userActions
    self.probs = probs
    self.releasedProb = 0.0
    self.releasedActions = 0
    self.numActions = numActions

  def __iter__(self):
    '''
    Iterating over an N-Best list yields a sequence of tuples, each:
      
      (userAction,prob,offListProb)
  
    where:
    
      userAction: UserAction instance for this NBest entry
      prob: probability that this UserAction is correct
      offListProb: the probability that any userAction, not yet enumerated on the N-Best list, is correct
    '''
    while (len(self.userActions) > 0):
      userAction = self.userActions.pop(0)
      prob = self.probs.pop(0)
      self.releasedProb += prob
      self.releasedActions += 1
      offListProb = 1.0 * (1.0 - self.releasedProb) / (self.numActions - self.releasedActions)
      yield (userAction,prob,offListProb)  
  
  def __str__(self):  
    resultList = ['  n asrProb userAction']
    for (n,userAction) in enumerate(self.userActions):
      prob = self.probs[n]
      resultList.append('%3d %1.5f %s' % (n,prob,userAction))
    return '\n'.join(resultList)

  def toString(self):
    return self.__str__()

