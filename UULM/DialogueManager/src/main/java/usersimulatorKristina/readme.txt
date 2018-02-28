User Simulation based on Agenda-based technique from Schatzmann et. al.

"usersimulatorKristina" package contains all necessary classes for the user simulator.

"usersimulatorKristina.dataextraction" package contains all necessary classes to extract a user model from database. 
For changing the model the classes can be extended or the SQL-statement can be changed (take care of preparedStatements).

"usersimulatorKristina.dialogueacts" contains all DialogueActs
with main class "DialogueAct.java" referring a "DialogueType.java".

./conf/OwlSpeak/models/US_Kristina/ contains data.json representing the user model
and usergoal.json containing possible user goals.


For starting the user simulator "UserSimulationKristina.java" shows some examples.

The UserSimulator.java contains four necessary methods for interaction:
	1) Initialize UserSimulator us = new UserSimulator();
	2) If you want the user to start the conversation: us.startConversation()
	3) For retrieving a User Move: us.getNextUserMove(SystemAction sa)
	4) For reseting User Goal and Agenda: us.resetUserSimulator()

26.09.17