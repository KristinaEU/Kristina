# These are the REQUEST acts for LetsGoPublic

# [03-14-03] (blangner) - updated to use array refs instead of subroutines
# [02-07-03] updated by blangner
# [11-05-02] antoine
#

$Rosetta::NLG::act{"timeout"} = {

    # request nothing (just wait for the user to say something)
    "nothing" => "",

    # prompts the user to make a query
    "next_query" => ["What else can I do for you?",
		     "Is there anything else I can do for you?"]

}
