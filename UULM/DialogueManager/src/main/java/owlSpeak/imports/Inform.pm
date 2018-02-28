# These are the INFORM acts for LetsGoPublic

# [2004-11-30] (antoine) started working on this, based on the
#                        LetsGoPublic NLG
#

my $first_nonunderstanding_of_dialog = 1;

$Rosetta::NLG::act{"inform"} = {

  ###############################################################
  # Generic, error handling informs
  ###############################################################
      
  # system informs the user that they need to speak less loud
  "nonunderstanding_speak_less_loud" =>
    [ "{audio src=\"../../Resources/audio/playback_00008.wav\"} ".
      "Sorry, I understand people best when they speak softer. {/audio}",
      "{audio src=\"../../Resources/audio/playback_00009.wav\"}".
      "I can't understand loud speech. Please speak more quietly. {/audio}"], 
     
    
  # system notifies the user that it's starting over after a non-understanding
  "nonunderstanding_starting_over" =>
    "{audio src=\"../../Resources/audio/playback_00053.wav\"} ".
    "Okay, let's start from the beginning. By the way, you can say ‘help' to ".
    "get tips for how to best use this system. {/audio}",

  # system notifies the user that it will give up
  "nonunderstanding_giveup" => sub {
    my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
    my $start_time = 7;
    if(($hour < 8) && (($wday == 0) || ($wday == 7))) { 
      $start_time = 8;
    }
    if(($hour >= 8) && (($wday == 6) || ($wday == 7))) { 
      $start_time = 8;      
    }
    if($start_time == 7) {
      return 
        "{audio src=\"../../Resources/audio/playback_00056.wav\"} ".
        "I'm very sorry, but it doesn't seem like I'm able to help you. ".
        "If your question can wait until 7, you can call back then to ".
        "speak to a person.  I hope that I can be more helpful the next time ".
        "you call. Thank you for calling the CMU Let's Go bus information ".
        "system. Goodbye. {/audio}";
    } else {
      return 
        "{audio src=\"../../Resources/audio/playback_00057.wav\"} ".
        "I'm very sorry, but it doesn't seem like I'm able to help you. ".
        "If your question can wait until 8, you can call back then to ".
        "speak to a person.  I hope that I can be more helpful the next time ".
        "you call. Thank you for calling the CMU Let's Go bus information ".
        "system. Goodbye. {/audio}";
    }
  },
    
  # system gives the user interaction tips
  "nonunderstanding_interaction_tips" => 
    "{audio src=\"../../Resources/audio/playback_00054.wav\"} ".
    "Okay, I know this conversation isn't going well. There are things you can". 
    " try to help me understand you better. Speak clearly and naturally; ". 
    " don't speak too quickly or too slowly. Give short, concise answers. ".      
    "Calling from a quiet place helps. If you'd like to start from scratch, ".
    "you can say 'start-over' at any time. {/audio}",      
    
  # asking the user for a shorter answer
  "nonunderstanding_askshortanswer" => 
    [ "{audio src=\"../../Resources/audio/playback_00037.wav\"}".
        "Please use shorter answers because I have trouble understanding long ". 
        "sentences. {/audio}",
      "{audio src=\"../../Resources/audio/playback_00036.wav\"}".
        "I need you to give me a short answer. {/audio}"],

  # system yields the floor
  "nonunderstanding_yield" => "",
  
  # a non-understanding move_on (in the let's go system this is empty)
  "nonunderstanding_moveon" => "",
  "nonunderstanding_subsequent_moveon" => "",
    
  ###############################################################
  # Greetings
  ###############################################################

  # welcome to the system
  "welcome" =>
    "{audio src=\"../../Resources/audio/intro1.wav\"} ".
		"Welcome to the CMU Let's Go bus information system.{/audio}",
  "recording_warning" =>
    "{audio src=\"../../Resources/audio/intro2.wav\"} ".
		"This conversation will be recorded for technical research purposes.".
		"{/audio}",
#	"system_description" =>	"{audio src=\"../../Resources/audio/intro3.wav\"}".
#		"I am a voice-activated agent that can give you schedule information for ".
#		"some bus routes running in the East End, Downtown, and the airport.".
#		"{/audio}",
	"how_to_get_help" =>	"{audio src=\"../../Resources/audio/intro4.wav\"} ".
		"To get help at any time, just say Help or press zero.{/audio}",
#	"welcome" =>			"One.",
#	"recording_warning" =>	"Two.",
#	"how_to_get_help" =>	"Three.",

	"system_description_slower" =>
    "{audio src=\"../../Resources/audio/slowintro3.wav\"} ".
		"I am a voice-activated agent that can give you schedule information for ".
		"some bus routes running in the East End, Downtown, and the airport.".
		"{/audio}",
	"how_to_get_help_slower" => 
    "{audio src=\"../../Resources/audio/slowintro4.wav\"} ".
		"To get help at any time, just say Help or press zero.{/audio}",

  ###############################################################
  # Structural Messages
  ###############################################################

  # can't hear user
  "speak_louder" => 
    "I'm having some trouble hearing you.  If you're still there, please ".
    "try to talk a little bit louder or closer to the phone.",

  # user not responding
  "you_not_there" => "I will assume you are not there. Goodbye.",
  "sorry_i_will_shutdown" => "I will hang up now.",

  # general help
  "generic_tips" =>
    "To help me understand you better, please go to a quiet place and ".
    "speak clearly in a natural voice.",

  # no help
  "no_help_available" => "I'm sorry, there is currently no help available.",

  # quitting
  "goodbye" => 
    "Thank you for using the cmu Let's Go Bus Information System. Goodbye.",

	# looking up database (to announce a delay)
	"looking_up_database_first" => 
    [ "Just a minute. Let me check that for you. {BREAK TIME=\"0.5s\"/}", 
		  "Just a minute. I'll look that up. {BREAK TIME=\"0.5s\"/}",
			"Hold on. Let me check that for you. {BREAK TIME=\"0.5s\"/}", 
			"Hold on. I'll look that up. {BREAK TIME=\"0.5s\"/}"],

	"looking_up_database_subsequent" => 
    [	"Just a second. {BREAK TIME=\"0.8s\"/}", 
			"Okay. {BREAK TIME=\"0.5s\"/}"],

  # produces an acknowledgment after the user
  # confirmed a concept
  "confirm_okay" => ["Okay", "Alright", "Right"],
    
    # backchannel
    "backchannel_uhu" => "{audio src=\"../../Resources/audio/uhu.wav\"} uhu {/audio}",

    # informs the user that there was a non-understanding
#    "nonunderstanding" => ["Sorry, I didn't understand what you said.",
#			"Sorry, I didn't catch that.",
#			"Sorry, I don't think I understood you correctly.",
#			"I'm sorry, I'm not sure I understood what you said.",
#			"I'm sorry, I didn't catch that.",
#			"I'm sorry, I don't think I understood you correctly."],
    "nonunderstanding" => 
      [ "Sorry, I didn't understand what you said.",
			  "Sorry, I didn't catch that."],
				
    # informs the user that there was a subsequent non-understanding
#    "subsequent_nonunderstanding" =>
#       ["I apologize, but I still did not understand you.",
#        "Sorry, I'm still having trouble understanding you."],
    "subsequent_nonunderstanding" =>
       ["Sorry, I still did not understand you.",
        "Sorry, I still didn't catch that.",
        "I'm still having trouble understanding you."],

    # informs the user that a request just failed
    "failrequest" => "Sorry, I didn't catch that.",

    # informs the user that a request has failed (after a number of nonunderstandings
    "subsequent_failrequest" => "Sorry, I still didn't catch that.",

	# when the user asked to quit...
	"quitting" => "I'm sorry I couldn't be more helpful. Please try again later.",

	# starting a new query after a "START OVER" or after completing a query
	"starting_over" => "Okay, let's start from the beginning.",
	"starting_new_query" => "Okay, let's start from the beginning.",

	# indicates that the requested bus route is not currently covered by the system
	"uncovered_route" =>	"I'm sorry but I do not have the schedule for the <uncovered_route>. ".
							"The routes I currently cover are the following: ".
							"28X, 54C, 56U, 59U, 61A, 61B, 61C, 61D, 61F, 64A, 69A, and 5 oh 1. ",

	# informs the user that we are using an alternative to get the departure stop
	"neighborhood_strategy" => "Let's procede step by step.",

	# indicates that the requested bus route is not currently covered by the system
	"uncovered_place" =>	"I'm sorry but I do not have the schedule for buses serving <uncovered_place.name>. ".
									"{BREAK TIME=\"0.5s\"/} Currently, I only cover some routes in the following neighborhoods: ".
									"Downtown, Oakland, Shadyside, Squirrel Hill, Homestead, McKeesport, and the airport. ",

	# indicates that no stops were found in the DB for the given place
	"no_stop_matching" => "I'm sorry but I do not know where <place.name> is located. Try using the closest intersection instead.",

	# indicates that no stops were found in the DB for the given place
	"too_many_stops_matching" => "I'm sorry but <place.name> is not specific enough. Please give me a landmark or intersection.",

	# indicates that the system will use a default stop in the given neighborhood
	"using_default_stop" => "Okay, I'll find a stop for you.",

	# Message given when the system is out for maintenance
	"maintenance_1" => "{audio src=\"../../Resources/audio/thankforcall.wav\"}{/audio}",
	"maintenance_2" => "{audio src=\"../../Resources/audio/maint-unavail.wav\"}{/audio}",
	"maintenance_3" => "{audio src=\"../../Resources/audio/callbackhours.wav\"}{/audio}",

    ###############################################################
    # Query results
    ###############################################################

    "result" => sub {
		my %args = @_;
		
		my $HOW_TO_GO = 1;
		my $LEAVE_AFTER = 2;
		my $ARRIVE_BEFORE = 3;
		my $BUS_AFTER_THAT = 4;
		my $BUS_BEFORE_THAT = 5;
		my $FIRST_BUS = 6;
		my $LAST_BUS = 7;
		
		my $departure_stop_name = getDepartureStopName(%args); #"<query.departure_place.name>";
		my $arrival_stop_name = getArrivalStopName(%args); #"<query.arrival_place.name>";
		my $departure_stop_result = getDepartureStopResult(%args);
		my $arrival_stop_result = getArrivalStopResult(%args);
		my $query_route_number = getQueryRouteNumber(%args);
		my $result_route_number = getResultRouteNumber(%args);
		my @line_number = getRouteIDs(%args);
		my @departure_time = getPronuncableDepartureTimes(%args);
		my @arrival_time = getPronuncableArrivalTimes(%args);

		if (($args{"query.type"} == $LEAVE_AFTER) && $args{"query.travel_time.time.now"}) 
		{
		    # informs the user of the time of the next bus
		    my $ret = "{s} The next $result_route_number leaves $departure_stop_result at $departure_time[0] {/s}";
		    if ($arrival_time[0] != -1)
		    {
			$ret .= " {s} and arrives at $arrival_stop_result at $arrival_time[0]. {/s}";
		    }
		    return $ret;
		}
		elsif ($args{"query.type"} == $LEAVE_AFTER) 
		{
		    # informs the user of the time of the next bus
		    my $ret = "{s} There is a $result_route_number leaving $departure_stop_result at $departure_time[0]. {/s}";
		    if ($arrival_time[0] != -1)
		    {
			$ret .= " {s} It will arrive at $arrival_stop_result at $arrival_time[0]. {/s}";
		    }
		    return $ret;
		}
		elsif (($args{"query.type"} == $ARRIVE_BEFORE) && $args{"query.travel_time.time.now"}) 
		{
		    # informs the user of the time of the next bus
		    my $ret = "The next $result_route_number arrives at $arrival_stop_result at $arrival_time[0].";
		    return $ret;
		}
		elsif ($args{"query.type"} == $ARRIVE_BEFORE) 
		{
		    # informs the user of the time of the next bus
		    my $ret = "{s} There is a $result_route_number leaving $departure_stop_result at $departure_time[0]. {/s}";
		    if ($arrival_time[0] != -1)
		    {
			$ret .= " {s} It will arrive at $arrival_stop_result at $arrival_time[0]. {/s}";
		    }
		    return $ret;
		}
		elsif (($args{"query.type"} == $BUS_BEFORE_THAT) || ($args{"query.type"} == $BUS_AFTER_THAT)) 
		{
		    # informs the user of the time of the next bus
		    my $ret = "{s} There is a $result_route_number leaving $departure_stop_result at $departure_time[0]. {/s}";
		    if ($arrival_time[0] != -1)
		    {
			$ret .= " {s} It will arrive at $arrival_stop_result at $arrival_time[0]. {/s}";
		    }
		    return $ret;
		}
		elsif ($args{"query.type"} == $FIRST_BUS) 
		{
		    my $ret;
		    if ($args{"query.route_number"} ne "") 
		    {
			$ret = "{s} The first $query_route_number of the day leaves $departure_stop_result at $departure_time[0]. {/s}";
		    }
		    else 
		    {
			$ret = "{s} The first $result_route_number of the day leaves $departure_stop_result at $departure_time[0]. {/s}";
		    }
		    
		    if ($arrival_time[0] != -1)
		    {
			$ret .= " {s} It arrives at $arrival_stop_result at $arrival_time[0]. {/s}";
		    }

		    return $ret;
		}
		elsif ($args{"query.type"} == $LAST_BUS) 
		{
		    my $ret;
		    if ($args{"query.route_number"} ne "") 
		    {
			$ret = "{s} The last $query_route_number of the day leaves $departure_stop_result at $departure_time[0]. {/s}";
		    }
		    else 
		    {
			$ret = "{s} The last $result_route_number of the day leaves $departure_stop_result at $departure_time[0]. {/s}";
		    }
		    
		    if ($arrival_time[0] != -1)
		    {
			$ret .= " {s} It arrives at $arrival_stop_result at $arrival_time[0]. {/s}";
		    }

		    return $ret;
		}
		elsif ($args{"query.type"} == $HOW_TO_GO) 
		{
		    $args{"result.routes"} =~ /&ARRAY\[(\d+)\]/;
		    my $n_routes = $1;
		    
		    if ($n_routes > 1) 
		    {
			
			# several bus routes
			my $res_str = "{s} There are $n_routes lines that run between $departure_stop_name and $arrival_stop_name: {/s} {s}";
			my $i = 0;
			for ($i = 0; $i < $n_routes-1; $i++)
			{
			    $res_str .= "the <result.routes.$i.route_number>, ";
			}
			$res_str .= "and the <result.routes.$i.route_number>. {/s}";
			return $res_str;
		    }
		    else
		    {
			return "The <result.routes.0.route_number> runs between $departure_stop_name and $arrival_stop_name.";
		    }
		}
    },
				    
    # gives the current time to the user
    "current_time" => sub {
		my %args = @_;
		my $thetime = convertTime($args{"currenttimeresult"});
		return ["It is currently <currenttimeresult $thetime>.", "The time is now <currenttimeresult $thetime>."];
    },

	# deals with "I want to speak to a person"-type requests
	"no_operator_available" => ["I'm sorry, but I'm the only one here right now. ".
				    "If you want to speak to a person, you will have to ".
				    "call back during normal business hours, which are ".
				    "Monday through Friday, 7 a. m. to 7 p. m., and ".
				    "weekends and holidays, 8 a. m. to 6 p. m."],

    ###############################################################
    # Query errors: tells the user that something is wrong with 
    #               his/her query
    ###############################################################

    # generic error
    "error" => sub {
        my $STOP_NOT_FOUND = 1;
		my $ROUTE_NOT_FOUND = 2;
		my $TIME_NOT_FOUND = 3;
		my $SAME_START_END = 4;
		my $NOT_ENOUGH_INFO = 5;
		my $BAD_QUERY = 6;
		my $STOP_NOT_ON_ROUTE = 7;
		my $SECONDARY_TIME_FAILED = 8;
		my $TIMEOUT_TRIGGERED = 9;
        my $ARRIVAL_NOT_FOUND = 10;
        my $DEPARTURE_NOT_FOUND = 11;
		my $ARRIVE_TIME_NOT_FOUND = 12;
		my $DEPART_TIME_NOT_FOUND = 13;
		my $STOPS_NOT_ON_ROUTE = 15;
		my $NOT_A_ROUTE = 16;
		my $NO_BUS_AFTER_THAT = 17;
		my $NO_BUS_BEFORE_THAT = 18;
		my $NO_BUS_AT_THAT_TIME = 19;
		my $INTERNAL_ERROR = 500;
		my $UNKNOWN_QUERY_TYPE = 999;

                my %args = @_;
                
		my $departure_stop_name = getDepartureStopName(%args);
		my $arrival_stop_name = getArrivalStopName(%args);
		my $query_route_number = getQueryRouteNumber(%args);
		my $result_route_number = getResultRouteNumber(%args);
		my @line_number = getRouteIDs(%args);
		my @departure_time = getPronuncableDepartureTimes(%args);
		my @arrival_time = getPronuncableArrivalTimes(%args);

       
		if ($args{"result.failed"} == $ARRIVAL_NOT_FOUND)
		{
		    return ["I'm sorry, I don't know any stops for $arrival_stop_name.",
			    "I'm sorry, I don't know of any stops for $arrival_stop_name."];
		}
		elsif ($args{"result.failed"} == $DEPARTURE_NOT_FOUND)
		{
		    return ["I'm sorry, I don't know any stops for $departure_stop_name.",
			    "I'm sorry, I don't know of any stops for $departure_stop_name."];
		}
        elsif ($args{"result.failed"} == $STOP_NOT_FOUND)
		{
		    return "I'm sorry, the stop that I think you described is unknown.  Please try again.",
		}
		elsif ($args{"result.failed"} == $NOT_A_ROUTE)
		{
		    return "I'm sorry, but the $query_route_number is not a known route.  Please try again.";
		}
		elsif ($args{"result.failed"} == $ROUTE_NOT_FOUND)
		{
		    return "I'm sorry, I don't know any routes that go from $departure_stop_name to $arrival_stop_name.";
		}
		elsif ($args{"result.failed"} == $SAME_START_END)
		{
		    return "$departure_stop_name and $arrival_stop_name are both the same stop.  Please provide a different start or end point.";
		}
		elsif ($args{"result.failed"} == $ARRIVE_TIME_NOT_FOUND)
		{
		    return "I'm sorry, but there's no time information available for $arrival_stop_name.";
		}
		elsif ($args{"result.failed"} == $DEPART_TIME_NOT_FOUND)
		{
		    return "I'm sorry, but there's no time information available for $departure_stop_name.";
		}
		elsif ($args{"result.failed"} == $STOPS_NOT_ON_ROUTE)
		{
		    return ["I'm sorry, but the $query_route_number does not go between $departure_stop_name and $arrival_stop_name.",
			    "I'm sorry, but the $query_route_number does not run between $departure_stop_name and $arrival_stop_name."];
		}
		elsif ($args{"result.failed"} == $NOT_ENOUGH_INFO)
		{
		    return "I'm sorry, but you did not provide enough information for me to complete your request.  Please try again.";
		}
		elsif ($args{"result.failed"} == $NO_BUS_AFTER_THAT)
		{
			if ($args{"query.route_number"}) {
				return ["I'm sorry, but that was the last $query_route_number between $departure_stop_name and $arrival_stop_name today.",
						"I'm sorry, but that was the last $query_route_number of the day between $departure_stop_name and $arrival_stop_name."];	
			}
			else {
				return ["I'm sorry, but that was the last bus between $departure_stop_name and $arrival_stop_name today.",
						"I'm sorry, but that was the last bus of the day between $departure_stop_name and $arrival_stop_name."];
			}
		}
		elsif ($args{"result.failed"} == $NO_BUS_BEFORE_THAT)
		{
			if ($args{"query.route_number"}) {
			    return "I'm sorry, but that was the first $query_route_number of the day between $departure_stop_name and $arrival_stop_name.";
			}
			else {
			    return "I'm sorry, but that was the first bus of the day between $departure_stop_name and $arrival_stop_name.";
			}
		}
		elsif ($args{"result.failed"} == $NO_BUS_AT_THAT_TIME)
		{
			my $at_that_time = "at that time";
			$at_that_time = "for the rest of the day" if ($args{"query.travel_time.time.now"});
			if ($args{"query.route_number"}) {
				return "I'm sorry, but there is no $query_route_number between $departure_stop_name and $arrival_stop_name $at_that_time.";
			}
			else {
				return ["I'm sorry, but there is no bus running between $departure_stop_name and $arrival_stop_name $at_that_time.",
						"I'm sorry, but there is no bus that goes between $departure_stop_name and $arrival_stop_name $at_that_time."];
			}
		}
		elsif ($args{"result.failed"} == $INTERNAL_ERROR)
		{
		    return "I'm sorry, I seem to have had an internal error while handling your request.  Please try again later.";
		}
		elsif ($args{"result.failed"} == $BAD_QUERY || $args{"result.failed"} == $UNKNOWN_QUERY_TYPE)
		{
		    return "I'm sorry, your request seems to have confused me.  Please try again.";
		}
		elsif ($args{"result.failed"} == $TIMEOUT_TRIGGERED)
		{
		    return ["I'm sorry, your request has too many results.  Please be more specific.",
			    "I'm sorry, your request has too many results.  Please try to narrow your request."];
		}
		else
		{
		    return "I'm sorry, I could not find any results for your request.  Please try again.";
		}
    },

};


# generates a name for the departure stop
sub getDepartureStopName 
{
    my %args = @_;
    my $stop_name;

    if ($args{"query.departure_place.name"} eq "C M U" or
	$args{"query.departure_place.name"} eq "CMU")
    {
	$stop_name = "<query.departure_place.name cmu>";
    }
    elsif ($args{"query.departure_place.name"} =~ /GREATER PITTSBURGH AIRPORT/i)
    {
        $stop_name = "<query.departure_place.name THE AIRPORT>";
    }
    elsif ($args{"query.departure_place.name"} =~ /(.+) #(.+)/i)
    {
        $stop_name = "<query.departure_place.name $1 NUMBER $2>";
    }
    else
    {
	$stop_name = "<query.departure_place.name>";
    }
    
    return $stop_name;	
}

# generates a name for the arrival stop
sub getArrivalStopName 
{
    my %args = @_;
    my $stop_name;
    
    if ($args{"query.arrival_place.name"} eq "C M U" or 
	$args{"query.arrival_place.name"} eq "CMU")
    {
	$stop_name = "<query.arrival_place.name cmu>";
    }
    elsif ($args{"query.arrival_place.name"} =~ /GREATER PITTSBURGH AIRPORT/i)
    {
        $stop_name = "<query.arrival_place.name THE AIRPORT>";
    }
    elsif ($args{"query.arrival_place.name"} =~ /(.+) #(.+)/i)
    {
        $stop_name = "<query.arrival_place.name $1 NUMBER $2>";
    }
    else
    {
	$stop_name = "<query.arrival_place.name>";
    }
    
    return $stop_name;	
}

# generates a name for the departure stop
sub getDepartureStopResult
{
    my %args = @_;
    my $stop_name;

    if ($args{"result.rides.0.departure_stop.name"} =~ /GREATER PITTSBURGH AIRPORT/i)
    {
        $stop_name = "<result.rides.0.departure_stop.name THE AIRPORT>";
    }
    elsif ($args{"result.rides.0.departure_stop.name"} =~ /GABRIEL'S PARKING LOT/i)
    {
        $stop_name = "<result.rides.0.departure_stop.name CENTURY SQUARE>";
    }
    elsif ($args{"result.rides.0.departure_stop.name"} =~ /ZOO CIRCLE DRIVE/i)
    {
        $stop_name = "<result.rides.0.departure_stop.name PITTSBURGH ZOO>";
    }
    elsif ($args{"result.rides.0.departure_stop.name"} =~ /CENTURY THREE FIRE LANE/i)
    {
        $stop_name = "<result.rides.0.departure_stop.name CENTURY THREE MALL>";
    }
    elsif ($args{"result.rides.0.departure_stop.name"} =~ /SOUTH HILLS JUNCTION AT BUS TURNAROUND/i)
    {
        $stop_name = "<result.rides.0.departure_stop.name SOUTH HILLS JUNCTION>";
    }
    elsif ($args{"result.rides.0.departure_stop.name"} =~ /(.+) #(.+)/i)
    {
        $stop_name = "<result.rides.0.departure_stop.name $1 NUMBER $2>";
    }
    else
    {
	$stop_name = "<result.rides.0.departure_stop.name>";
    }
    
    return $stop_name;	
}

# generates a name for the arrival stop
sub getArrivalStopResult
{
    my %args = @_;
    my $stop_name;
    
    if ($args{"result.rides.0.arrival_stop.name"} =~ /GREATER PITTSBURGH AIRPORT/i)
    {
        $stop_name = "<result.rides.0.arrival_stop.name THE AIRPORT>";
    }
    elsif ($args{"result.rides.0.arrival_stop.name"} =~ /GABRIEL'S PARKING LOT/i)
    {
        $stop_name = "<result.rides.0.arrival_stop.name CENTURY SQUARE>";
    }
    elsif ($args{"result.rides.0.arrival_stop.name"} =~ /ZOO CIRCLE DRIVE/i)
    {
        $stop_name = "<result.rides.0.arrival_stop.name PITTSBURGH ZOO>";
    }
    elsif ($args{"result.rides.0.arrival_stop.name"} =~ /CENTURY THREE FIRE LANE/i)
    {
        $stop_name = "<result.rides.0.arrival_stop.name CENTURY THREE MALL>";
    }
    elsif ($args{"result.rides.0.arrival_stop.name"} =~ /SOUTH HILLS JUNCTION AT BUS TURNAROUND/i)
    {
        $stop_name = "<result.rides.0.arrival_stop.name SOUTH HILLS JUNCTION>";
    }
    elsif ($args{"result.rides.0.arrival_stop.name"} =~ /(.+) #(.+)/i)
    {
        $stop_name = "<result.rides.0.arrival_stop.name $1 NUMBER $2>";
    }
    else
    {
	$stop_name = "<result.rides.0.arrival_stop.name>";
    }
    
    return $stop_name;	
}

# processes a route id so that it's pronounced properly
sub getRouteIDs {
	my %args = @_;
	
	my @result = ();
	
	if ($args{"result.routes"} =~ /&ARRAY\[(\d+)\]/) 
	{
	    my $num_results = $1;
	    for (my $i = 0; $i < $num_results; $i++) 
	    {
		my $temp = $args{"result.routes.$i.route_number"};
		my $id = "<result.routes.$i.route_number $temp>";
		#$id =~ s/^(\d+)A$/$1 ay/;
		#$id =~ s/^(\d+)([B-Z])$/$1 $2/;
		$id =~ s/501/5 oh 1/;
		push(@result, $id);
	    }
	}
	
	return @result;
}

sub getQueryRouteNumber {
    my %args = @_;
    my $rnum = $args{"query.route_number"};
    if ($rnum eq '501')
    {
	return "<query.route_number 5 oh 1>";
    }
    elsif ($rnum eq 'EBO')
    {
	return "<query.route_number E.B.O.>";
    }
    elsif ($rnum eq 'EBA')
    {
	return "<query.route_number E.B.A.>";
    }
    else
    {
	return "<query.route_number>";
    }
}

sub getResultRouteNumber {
    my %args = @_;
    my $rnum = $args{"result.rides.0.route_number"};
    if ($rnum eq '501')
    {
	return "<result.rides.0.route_number 5 oh 1>";
    }
    elsif ($rnum eq 'EBO')
    {
	return "<result.rides.0.route_number E.B.O.>";
    }
    elsif ($rnum eq 'EBA')
    {
	return "<result.rides.0.route_number E.B.A.>";
    }
    else
    {
	return "<result.rides.0.route_number>";
    }
}

# gets all the departure times in a pronuncable form
sub getPronuncableDepartureTimes {
	my %args = @_;
	
	my @result = ();
	
	if ($args{"result.rides"} =~ /&ARRAY\[(\d+)\]/) 
	{
	    my $num_results = $1;
	    for (my $i = 0; $i < $num_results; $i++) 
	    {
		my $t;
		if (defined $args{"result.rides.$i.departure_time"}) 
		{
		    my $temp = &convertTime($args{"result.rides.$i.departure_time"});
		    $t = "<result.rides.$i.departure_time $temp>";
		} 
		else 
		{
		    $t = -1;			
		}
		
		push( @result, $t);
	    }
	}
	
	return @result;
}

# gets all the arrival times in a pronuncable form
sub getPronuncableArrivalTimes {
	my %args = @_;
	
	my @result = ();
	
	if ($args{"result.rides"} =~ /&ARRAY\[(\d+)\]/) 
	{
	    my $num_results = $1;
	    for (my $i = 0; $i < $num_results; $i++) 
	    {
		my $t;
		if (defined $args{"result.rides.$i.arrival_time"}) 
		{
		    my $temp = &convertTime($args{"result.rides.$i.arrival_time"});
		    $t = "<result.rides.$i.arrival_time $temp>";
		} 
		else 
		{
		    $t = -1;			
		}
		
		push( @result, $t);
	    }
	}
	
	return @result;
}

#####
1;  # end module
#####
