# These are the Establish Context acts for LetsGoPublic

# [04-11-03] created by antoine
#

$Rosetta::NLG::act{"establish_context"} = {

     "get_query_specs" => sub {
		my %args = @_;
		
		my $mytime = convertTime($args{"query.travel_time.time.value"});

		if ((!$args{"query.departure_place.name"})&&
		    (!$args{"query.route_number"})) {
		   return	"{s} I am an automated spoken dialogue system ".
					"that can give you schedule information for ".
					"bus routes in Pittsburgh's East End. {/s}  ".
					"{s} You can ask me about the following buses: ".
					"28X, 54C, 56U, 59U, 61A, 61B, 61C, 61D, 61F, 64A, 69A, and 501. {/s}";
		}

		my $prompt = "So far, I know that you want to take the ";
		if ($args{"query.travel_time.time.now"}) {
			$prompt .= "next ";
		}
		
		
		if ($args{"query.route_number"}) {
			$prompt .= "<query.route_number> ";
		}
		else {
			$prompt .= "bus ";
		}

		if ($args{"query.departure_place.name"}) {
			$prompt .= "from <query.departure_place.name> ";
		}
		if ($args{"query.arrival_place.name"}) {
			$prompt .= "to <query.arrival_place.name> ";
		}
		if (!$args{"query.travel_time.time.now"}&&
			($args{"query.travel_time.time.type"} eq "departure")) {
			$prompt .= "<query.travel_time.time leaving at around $mytime> ";
		}
		elsif ($args{"query.travel_time.time.type"} eq "arrival") {
			$prompt .= "<query.travel_time.time to arrive by $mytime>. ";
		}


		return $prompt;
     },

	"get_departure_neighborhood_and_stop" => sub {
		my %args = @_;
		
		my $prompt = "";
		
		if ($args{"query.route_number"}) {
			$prompt .= "So far, I know that you want to take the ";
			if ($args{"query.travel_time.time.now"}) {
				$prompt .= "next ";
			}
			$prompt .= "<query.route_number>";
		}
		
		if ($args{"neighborhood"} ne "<UNDEFINED>") {			
			if (!$args{"query.route_number"}) {
				$prompt .= "So far, I know that you want to take the ";
				if ($args{"query.travel_time.time.now"}) {
					$prompt .= "next ";
				}
				$prompt .= "bus ";
			}
						
			$prompt .= " from <neighborhood.name>.";		
		}
		else {
			$prompt .= ". {BREAK TIME=\"0.5s\"/} " if $prompt ne "";
			
			$prompt .= "Since I was having difficulty understanding ";
			$prompt .= "your departure place, we are now trying to ";
			$prompt .= "describe it progressively.";
		}
	},

	"disambiguate_departure_arrival_place" => "{s} {BREAK TIME=\"0.5s\"/} I understood you mentioned <query.departure_place.name> {/s} ".
											  "{s} {BREAK TIME=\"0.3s\"/} but I don't know if this is where you will ".
											  "leave from or if it's the place you want to go to.{/s}",

	"give_results" => sub {
		my %args = @_;

		my $mytime = convertTime($args{"query.travel_time.time.value"});

		my $prompt = "{s} I looked for ";
		if ($args{"query.travel_time.time.now"}) {
			$prompt .= "the next ";
		}
		else {
			$prompt .= "a ";
		}
		
		if ($args{"query.route_number"}) {
			$prompt .= "<query.route_number> ";
		}
		else {
			$prompt .= "bus ";
		}

		if (!$args{"query.travel_time.time.now"}&&
			($args{"query.travel_time.time.type"} eq "departure")) {
			$prompt .= "leaving <query.departure_place.name> ";
			$prompt .= "for <query.arrival_place.name> ";
			$prompt .= "at around <query.travel_time $mytime> ";
		}
		elsif ($args{"query.travel_time.time.type"} eq "arrival") {
			$prompt .= "to get to <query.arrival_place.name> ";
			$prompt .= "from <query.departure_place.name> ";
			$prompt .= "<query.travel_time by $mytime> ";
		}
		else {
			$prompt .= "from <query.departure_place.name> ";
			$prompt .= "to <query.arrival_place.name>. ";
		}

		$prompt .= "{/s} ";

		# Repeat the actual result

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
		my $INTERNAL_ERROR = 500;
		my $UNKNOWN_QUERY_TYPE = 999;

		my $departure_stop_name = getDepartureStopName(%args); #"<query.departure_place.name>";
		my $arrival_stop_name = getArrivalStopName(%args); #"<query.arrival_place.name>";
		my @line_number = getRouteIDs(%args);
		my @departure_time = getPronuncableDepartureTimes(%args);
		my @arrival_time = getPronuncableArrivalTimes(%args);

		$prompt .= "{s} ";

		if ($args{"result.failed"} == $ARRIVAL_NOT_FOUND)
		{
		    $prompt .= "However, I don't know any stops for $arrival_stop_name.";
		}
		elsif ($args{"result.failed"} == $DEPARTURE_NOT_FOUND)
		{
		    $prompt .= "However, I don't know any stops for $departure_stop_name.";
		}
                elsif ($args{"result.failed"} == $STOP_NOT_FOUND)
		{
		    $prompt .= "However, the stop that I think you described is unknown.",
		}
		elsif ($args{"result.failed"} == $NOT_A_ROUTE)
		{
		    $prompt .= "However, but the <query.route_number> is not a known route.";
		}
		elsif ($args{"result.failed"} == $ROUTE_NOT_FOUND)
		{
		    $prompt .= "However, I don't know any routes that go from $departure_stop_name to $arrival_stop_name.";
		}
		elsif ($args{"result.failed"} == $SAME_START_END)
		{
		    $prompt .= "However, $departure_stop_name and $arrival_stop_name are both the same stop.";
		}
		elsif ($args{"result.failed"} == $ARRIVE_TIME_NOT_FOUND)
		{
		    $prompt .= "Unfortunately, there's no time information available for $arrival_stop_name.";
		}
		elsif ($args{"result.failed"} == $DEPART_TIME_NOT_FOUND)
		{
		    $prompt .= "Unfortunately, there's no time information available for $departure_stop_name.";
		}
		elsif ($args{"result.failed"} == $STOPS_NOT_ON_ROUTE)
		{
		    $prompt .= "However, but the <query.route_number> DOES not run between $departure_stop_name and $arrival_stop_name.";
		}
		elsif ($args{"result.failed"} == $NOT_ENOUGH_INFO)
		{
		    $prompt .= "However, but you did not provide enough information for me to complete your request.";
		}
		elsif ($args{"result.failed"} == $INTERNAL_ERROR)
		{
		    $prompt .= "Unfortunately, I seem to have had an internal error while handling your request.";
		}
		elsif ($args{"result.failed"} == $BAD_QUERY || $args{"result.failed"} == $UNKNOWN_QUERY_TYPE)
		{
		    $prompt .= "Unfortunately, this request seems to have confused me and I wasn't able to complete it.";
		}
		elsif ($args{"result.failed"} == $TIMEOUT_TRIGGERED)
		{
		    $prompt .= "Unfortunately, this request has too many results.";
		}
		elsif ($args{"result.failed"})
		{
		    $prompt .= "Unfortunately, I could not find any results for this request.";
		}
		else {
			$prompt .= "I found a <result.rides.0.route_number> leaving <result.rides.0.departure_stop.name> at $departure_time[0].";
			$prompt .= "  and reaching <result.rides.0.arrival_stop.name> at $arrival_time[0].";
		}
		
		$prompt .= "{/s}";

		return $prompt;
	}

}

