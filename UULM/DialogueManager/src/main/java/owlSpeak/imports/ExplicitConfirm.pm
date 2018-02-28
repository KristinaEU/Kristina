# These are the EXPLICIT CONFIRM acts for LetsGoPublic

# [04-12-06] created by antoine
#

$Rosetta::NLG::act{"explicit_confirm"} = 
  {
						   
   # confirms both the departure and arrival place
   # only confirms parts that have changed since last confirmation
   "uncovered_route" => {	"default" => [	"{s} The <uncovered_route>. {/s} {s} Is this correct? {/s}",
						"{s} The <uncovered_route>. {/s} {s} Did I get that right? {/s}"],
				"explain_more" => "{s} I think you said you wanted the schedule of the <uncovered_route>. {/s} {s} I need you to confirm that. {/s}",
				"what_can_i_say" => "{s} If you want the schedule of the <uncovered_route> say yes or press one, {/s} {s} otherwise say no or press three."},
   "query.route_number" => sub {
     my %args = @_;
     my $query_route_number = getQueryRouteNumber(%args);
						     
     return { "default" => [	"{s} The $query_route_number.  {/s} {s} Is this correct? {/s}",
				"{s} The $query_route_number.  {/s} {s} Did I get that right? {/s}"],
	      "explain_more" => "{s} I think you said you wanted the schedule of the $query_route_number. {/s} {s} I need you to confirm that. {/s}",
	      "what_can_i_say" => "{s} If you want the schedule of the $query_route_number say yes or press one, {/s} {s} otherwise say no or press three. {/s}"}
   },
						   
   "query.departure_place" => {"default" => [	"{s} Leaving from <query.departure_place.name>. {/s} {s} Is this correct? {/s}",
				"{s} Leaving from <query.departure_place.name>. {/s} {s} Did I get that right? {/s}"],
	     "explain_more" => "{s} I think you said you wanted to leave from <query.departure_place.name>. {/s} {s} I need you to confirm that. {/s}",
	     "what_can_i_say" => "{s} If you want to leave from <query.departure_place.name> say yes or press one, {/s} {s} otherwise say no or press three. {/s}"},

   "neighborhood" => {	"default" => [	"{s} Leaving from <neighborhood.name>. {/s} {s} Is this correct? {/s}",							
					"{s} Leaving from <neighborhood.name>. {/s} {s} Did I get that right? {/s}"],
			"explain_more" => "{s} I think you said you wanted to leave from <neighborhood.name>. {/s} {s} I need you to confirm that. {/s}",
			"what_can_i_say" => "{s} If you want to leave from <neighborhood.name> say yes or press one, {/s} {s} otherwise say no or press three. {/s}"},
   "single_place" => {	"default" => [	"{s} <single_place.name>. {/s} {s} Is this correct? {/s}",
					"{s} <single_place.name>. {/s} {s} Did I get that right? {/s}"],
			"explain_more" => "{s} I think you said <single_place.name>. {/s} {s} I need you to confirm that. {/s}",
			"what_can_i_say" => "{s} If you want a bus for <single_place.name> say yes or press one, {/s} {s} otherwise say no or press three. {/s}"},
   "uncovered_place" => {	"default" => [	"{s} <uncovered_place.name>. {/s} {s} Is this correct? {/s}",
						"{s} <uncovered_place.name>. {/s} {s} Did I get that right? {/s}"],
				"explain_more" => "{s} I think you said you <uncovered_place.name>. {/s} {s} I need you to confirm that. {/s}",
				"what_can_i_say" => "{s} If you a bus for <uncovered_place.name> say yes or press one, {/s} {s} otherwise say no or press three. {/s}"},
   "query.arrival_place" => {	"default" =>  [	"{s} Going to <query.arrival_place.name>. {/s} {s} Is this correct? {/s}",
						"{s} Going to <query.arrival_place.name>. {/s} {s} Did I get that right? {/s}"],
				"explain_more" => "{s} I think you said you wanted to go to <query.arrival_place.name>. {/s} {s} I need you to confirm that. {/s}",
				"what_can_i_say" => "{s} If you want to go to <query.arrival_place.name> say yes or press one, {/s} {s} otherwise say no or press three. {/s}"},
   "query.travel_time" => sub {
     my %args = @_;
		
     my $mytime = convertTime($args{"query.travel_time.time.value"});	

     if ($args{"query.travel_time.time.type"} eq "departure") {
       if ($args{"query.travel_time.time.now"}) {
	 return {"default" => [	"{s} You want <query.travel_time the next bus>. {/s} {s} Is this correct? {/s}",
				"{s} I think you want <query.travel_time the next bus>. {/s} {s} Am I right? {/s}"],
		 "explain_more" => "{s} I think you said you want to take  <query.travel_time the next bus>. {/s} {s} I need you to confirm this. {s}",
		 "what_can_i_say" => "{s} If you want the time of <query.travel_time the next bus> say yes or press one, {/s} {s} otherwise, say no or press three. {/s}"}
       }
			
       return {"default" => [	"{s} <query.travel_time Leaving at $mytime>. {/s} {s} Is this correct? {/s}",
				"{s} <query.travel_time Leaving at $mytime>. {/s} {s} Did I get that right? {/s}"],
	       "explain_more" => "{s} I think you said you want to <query.travel_time leave at $mytime>. {/s} {s} I need you to confirm that. {/s}",
	       "what_can_i_say" => "{s} If you want to <query.travel_time leave at $mytime> say yes or press one, {/s} {s} otherwise say no or press three. {/s}"}
     } else {
		
       return {"default" => [	"{s} <query.travel_time Arriving by $mytime>. {/s} {s} Is this correct? {/s}",
				"{s} <query.travel_time Arriving by $mytime>. {/s} {s} Did I get that right? {/s}"],
	       "explain_more" => "{s} I think you said you want to <query.travel_time arrive before $mytime>. {/s} {s} I need you to confirm that. {/s}",
	       "what_can_i_say" => "{s} If you want to <query.travel_time arrive before $mytime> say yes or press one, {/s} {s} otherwise say no or press three. {/s}"}
     }
   }
  };
