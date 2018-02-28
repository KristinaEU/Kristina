# These are the REQUEST acts for LetsGoPublic

# [03-14-03] (blangner) - updated to use array refs instead of subroutines
# [02-07-03] updated by blangner
# [11-05-02] antoine
#

$Rosetta::NLG::act{"request"} = {

  ##############################################################################
  # Generic, error handling requests
  ##############################################################################

  # asking if the user is still there (on a timeout)
  "are_you_still_there" => "Are you still there?",					 

  # asking the user to repeat on a non-understanding
	"nonunderstanding_askrepeat" => 
    [ "{audio src=\"../../Resources/audio/playback_00042.wav\"}".
        "Can you say that again? {/audio}",
      "{audio src=\"../../Resources/audio/playback_00043.wav\"}".
        "Can you repeat that? {/audio}",
      "{audio src=\"../../Resources/audio/playback_00044.wav\"}".
        "Can you repeat what you just said? {/audio}" ],

  # asking the user to rephrase on a non-understanding
	"nonunderstanding_askrephrase" => 
    [ "{audio src=\"../../Resources/audio/playback_00047.wav\"}".
        "Can you rephrase that? {/audio}",
      "{audio src=\"../../Resources/audio/playback_00048.wav\"}".
        "Could you rephrase that? {/audio}" ],
			
  # asking the user for a shorter answer
  "nonunderstanding_askshortanswer" => 
    [ "{audio src=\"../../Resources/audio/playback_00037.wav\"}".
        "Please use shorter answers because I have trouble understanding long ". 
        "sentences. {/audio}",
      "{audio src=\"../../Resources/audio/playback_00036.wav\"}".
        "I need you to give me a short answer. {/audio}"],

  # asking the user if they want to start over on a non-understanding
	"nonunderstanding_askstartover" => 
    [ "{audio src=\"../../Resources/audio/playback_00049.wav\"}".
        "I'm sorry but I'm still having problems understanding you and ".
        "I might do better if we restarted.Would you like to start over?".
        "{/audio}" ],

  ##############################################################################
  # Task-specific requests
  ##############################################################################

  # request nothing (just wait for the user to say something)
  "nothing" => "...",

  # ask if the user wants to make another query
  "next_query" => {	
    "default"	=> "To get more information about buses related to this trip, ".
      "you can say, when is the next bus, or, when is the previous bus.  ". 
      "To ask about a different trip, you can say, start a new query.  ".
      "If you are finished, you can say goodbye.",
  	"explain_more" => "Right now, I need to know what you want to do next.",
  	"what_can_i_say"	=> "You can say, when is the next bus, when is the ".
      "previous bus, start a new query, or goodbye."
  },

  "next_query_error" => {	
    "default"	=> "To ask about a different trip, you can say, start a new ".
      "query. If you are finished, you can say goodbye.",
		"explain_more"	=> "Right now, I need to know what you want to do next.",
		"what_can_i_say"	=> "You can say start a new query, or goodbye."
  },

  # request the bus number or departure place (first request)
  "how_may_i_help_you" => {	
    "default"	=> sub {
  		my %args = @_;
  		if ($args{"system_version"} == 2) {
  			return "What bus information are you looking for?";
  		} else {
  			return "What can I do for you?";
  		}
	  },
		"explain_more" => "What bus schedule information are you looking for?",
		"what_can_i_say"	=> "For example, you can say, when is the next 28X from ".
      "DOWNTOWN to THE AIRPORT? or I'd like to go from MCKEESPORT to ".
      "HOMESTEAD tomorrow at 10 a.m.."
  },

  # request the bus number or departure place (first request)
  "busnumber_or_departureplace" => {	
    "default"	=> "Which bus number or departure stop do you want information for?",
		"explain_more" => "Right now, I need you to tell me either the number of ".
      "the bus you want to take or the name of the bus stop you're leaving from.",
		"what_can_i_say"	=> "For example, you can say, 28X, or FORBES AND MURRAY, ".
      "or, DOWNTOWN.",
		"what_can_i_say_or_startover"	=> "For example, you can say, 28X, or ".
      "FORBES AND MURRAY, or, DOWNTOWN, or say start over to restart."
  },

  # request the departure place
  "query.departure_place" => {	
    "default"	=> 
      [ "Where are you leaving from?",
				"Where do you want to leave from?",
				"Where would you like to leave from?",
				"Where do you wanna leave from?"],
		"explain_more" => "Right now, I need you to tell me which bus stop you're ".
      "leaving from.",
		"what_can_i_say" => sub {
			my %args = @_;
			return "For example, you can say, ".&getSampleStops(%args).".";
		},
		"what_can_i_say_or_startover" => sub {
			my %args = @_;
			return "For example, you can say, ".&getSampleStops(%args).", or say ".
        "start over to restart.";
		}
	},

  # request the departure place when a neighborhood has already been given
  "departure_stop_in_neighborhood" => {	
    "default"	=> 
      [ "Which stop in <neighborhood.name> are you leaving from?",
				"Where in <neighborhood.name> are you leaving from?",
				"Which stop in <neighborhood.name> do you want to leave from?",
				"Where in <neighborhood.name> do you want to leave from?"],
		"explain_more" => "Right now, I need you to tell me the exact bus stop or landmark you're leaving from.",
		"what_can_i_say"	=> sub {
			my %args = @_;
			return "For example, you can say, ".&getSampleStops(%args).". {BREAK TIME=\"0.3\"/}".
			"If you're not sure about the name of the stop, you can say I don't know.";
		}
	},

  # request the departure place
  "neighborhood" => {	
    "default"	=> 
      [ "Which neighborhood do you want to leave from?",
  			"In what neighborhood is your departure stop?"],
		"explain_more" => "Right now, I need you to tell me which neighborhood ".
      "you are leaving from.",
		"what_can_i_say"	=> "For example, you can say, SQUIRREL HILL, ".
      "{BREAK TIME=\"0.5s\"/} OAKLAND, {BREAK TIME=\"0.5s\"/} or THE AIRPORT.",
		"what_can_i_say_or_startover"	=> "For example, you can say, MCKEESPORT, ".
      "{BREAK TIME=\"0.5s\"/} or OAKLAND, {BREAK TIME=\"0.5s\"/} or say start ".
      "over to restart."
  },

  # request the travel time
  "query.travel_time" => { 
    "default"	=> 
      [ "When would you like to travel?", 
				"When are you going to take that bus?", 
				"When do you wanna travel?",
				"When do you want to travel?"],
		"explain_more"	=> "Right now, I need to know about what time you want ".
      "to take the bus.",
		"what_can_i_say"	=> "For example, you can say, 10:30 in ".
      "the morning, or I want to arrive by 3 p m, or i want the next bus.", 
		"what_can_i_say_or_startover"	=> "For example, you can say, ".
      "10:30 in the morning, or I want the next bus, or say start over to ".
      "restart."
  }, 
      	
	# request the exact travel time (when the user used a period qualifier like today)
	"exact_travel_time" => {
    "default" => 
      [ "At what time would you like to travel?", 
				"At what time do you wanna travel?",
				"At what time do you want to travel?"],
		"explain_more"	=> "Right now, I need to know about what time you want ".
      "to take the bus.",
		"what_can_i_say"	=> "For example, you can say, 10:30 in ".
      "the morning, or I want to arrive by 3 p m, or i want the next bus.",
		"what_can_i_say_or_startover"	=> "For example, you can say, ".
      "10:30 in the morning, I want the next bus, or say start over to ".
      "restart."
  }, 
      						   
  # request the arrival place
  "query.arrival_place" => {	
    "default" => 
      [	"Where are you going?",
				"Where do you want to go?",
				"Where do you wanna go?",
				"What is your destination?"],
		"explain_more"	=> "Right now, I need you to tell me the stop or ".
      "neighborhood you want to go to.",
		"what_can_i_say"	=> sub {
			my %args = @_;
			return "For example, you can say, ".&getSampleStops(%args).".";
		},
		"what_can_i_say_or_startover"	=> sub {
			my %args = @_;
			return "For example, you can say, ".&getSampleStops(%args).", or say ".
			  "start over to restart.";
		}
	},
	
	# disambiguates when the same stop name was heard for departure and destination
	"disambiguate_departure_arrival_place" => { 
    "default" => "Sorry, I must have misunderstood you. Is ".
      "<query.departure_place.name> your departure stop {BREAK TIME=\"0.3\"/} ".
      "or your destination?",
		"explain_more"	=> "Right now, I need you to tell me if it is your ".
      "departure stop or your destination.",
		"what_can_i_say" => "Please say departure or press one if you want to ".
      "leave from there, {BREAK TIME=\"0.3s\"/} or say destination or press ".
      "three if you want to go there."
  },

  # request the line number
  "query.line_number" => {
    "default"	=> "Which route do you want to take?",
		"explain_more" => "Right now, I need to know the specific bus route that ".
      "you want to take.",
		"what_can_i_say"	=> "For example, you can say, 61 C, or, 28 X.",
		"what_can_i_say_or_startover"	=> "For example, you can say, 61 C, or, ".
      "28 X, or say start over to restart."
  },
      
  # request the direction
  "query.direction" => {
    "default"	=> "Are you going towards downtown or away from downtown?",
		"explain_more" => "Right now, I need you to tell me the direction towards ".
      "which you want to go.",
		"what_can_i_say"	=> "You can say inbound or outbound.  You can also ".
      "say I'd like to go to THE AIRPORT."
  },

  # structural prompts

  # start over prompt
  "sure_start_over" => {
    "default"	=> "Are you sure you want to start over?",
	  "explain_more"	=> "I think you told me you wanted to restart this ".
      "dialogue from the beginning. I will forget about everything you have ".
      "told me so far. Is this what you want to do?",
    "what_can_i_say"	=> "If you want to start over say yes or press one, ".
      "to continue where we left off say no or press three."
  },

  # you awake over there?
  "are_you_still_there" => {
    "default"	=> "Are you still there?"
  },
    
};

sub convertTime
{
    my $hr, my $min;
    if ($_[0] =~ /(\d+)(\d\d)/)
    {
	$hr = $1;
	$min = $2;
    }
    else
    {
	$hr = 0;
	$min = $_[0];
    }

    my $suf = "a.m.";
    if ($hr > 12)
    {
        $suf = "p.m.";
	$hr -= 12;
    }
    elsif ($hr == 12)
    {
	$suf = "p.m.";
    }
    elsif ($hr == 0)
    {
	$hr = 12;
    }

    my $str;
    if ($min == 0)
    {
	$str = "$hr $suf";
    }
    elsif ($min < 10)
    {
        $str = sprintf( "$hr oh %.1d $suf", $min);
    }
    else
    {
	$str = "$hr $min $suf";
    }
    
    return $str;
}


# generates a name for an ambiguous place
sub getAmbiguousPlaceName 
{
    my %args = @_;
    my $stop_name;

    if ($args{"ambiguous_place.name"} eq "C M U" or 
	$args{"ambiguous_place.name"} eq "CMU")


    {
	$stop_name = "<ambiguous_place.name cmu>";
    }
    else
    {
	$stop_name = "<ambiguous_place.name>";
    }
    
    return $stop_name;	
}

# generates some example stops for a given route
sub getSampleStops {
	my %args = @_;
	
	my $ret = "";
	
	if ($args{"query.route_number"} eq "28X") {
		$ret .= "THE AIRPORT, DOWNTOWN, or OAKLAND";
	}
	elsif ($args{"query.route_number"} eq "54C") {
		$ret .= "SOUTH HILLS JUNCTION, FIFTH AT BIGELOW, or THE SOUTH SIDE";
	}		
	elsif ($args{"query.route_number"} eq "56U") {
		$ret .= "HAZELWOOD, WIGHTMAN AND BEACON, or THE CATHEDRAL OF LEARNING";
	}		
	elsif ($args{"query.route_number"} eq "59U") {
		$ret .= "FORBES AND ATWOOD, THE WATERFRONT, or CENTURY THREE MALL";
	}		
	elsif ($args{"query.route_number"} eq "61A") {
		$ret .= "NORTH BRADDOCK, FORBES AND MURRAY, or DOWNTOWN";
	}		
	elsif ($args{"query.route_number"} eq "61B") {
		$ret .= "BRADDOCK, FORBES AND MURRAY, or DOWNTOWN";
	}		
	elsif ($args{"query.route_number"} eq "61C") {
		$ret .= "MCKEESPORT, MURRAY AND HAZELWOOD, or DOWNTOWN";
	}		
	elsif ($args{"query.route_number"} eq "64A") {
		$ret .= "THE WATERFRONT, FORBES AND MURRAY, or EAST LIBERTY";
	}		
	elsif ($args{"query.route_number"} eq "69A") {
		$ret .= "DOWNTOWN, FORBES AND MURRAY, or WILKINSBURG";
	}		
	elsif ($args{"query.route_number"} eq "501") {
		$ret .= "ISLAND AND BEAVER, OAKLAND, or FORBES AND MURRAY";
	}		
	else {
		$ret .= "FORBES AND MURRAY, DOWNTOWN, or MCKEESPORT";
	}
	return $ret;
}
