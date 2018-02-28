# These are the EXPLICIT CONFIRM acts for LetsGoPublic

# [10-02-07] created by antoine based on ExplicitConfirm.pm
#

$Rosetta::NLG::act{"implicit_confirm"} = 
  {
						   
   # confirms both the departure and arrival place
   # only confirms parts that have changed since last confirmation
   "uncovered_route" => "{s} The <uncovered_route>. {/s}",
   "query.route_number" => sub {
     my %args = @_;
     my $query_route_number = getQueryRouteNumber(%args);
						     
     return "{s} The $query_route_number.  {/s}";
   },
						   
   "query.departure_place" => "{s} Leaving from <query.departure_place.name>. {/s}",

   "neighborhood" => "{s} Leaving from <neighborhood.name>. {/s}",							
   "single_place" => "{s} <single_place.name>. {/s}",
   "uncovered_place" => "{s} <uncovered_place.name>. {/s}",
   "query.arrival_place" => "{s} Going to <query.arrival_place.name>. {/s}",
   "query.travel_time" => sub {
     my %args = @_;
		
     my $mytime = convertTime($args{"query.travel_time.time.value"});	

     if ($args{"query.travel_time.time.type"} eq "departure") {
       if ($args{"query.travel_time.time.now"}) {
	 return "{s} <query.travel_time The next bus>. {/s}";
       }
			
       return "{s} <query.travel_time Leaving at $mytime>. {/s}";
     } else {
		
       return "{s} <query.travel_time Arriving by $mytime>. {/s}";
     }
   }
  };
