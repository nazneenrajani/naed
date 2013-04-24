# ------------------------------------------------------------------
# Computes the relatedness of two elements and gives the lesk score.
# http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lesk.pm
# ------------------------------------------------------------------


use WordNet::QueryData;
use WordNet::Similarity::lesk;


my $wn = WordNet::QueryData->new();
my $lesk = WordNet::Similarity::lesk->new($wn);

open INFILE, "edaena.txt" or die $!;
my @lines = <INFILE>;

my $phrase = "";
my $isLiterally;
foreach (@lines) {
    my $first_char = substr($_, 0, 1);
 	my $word;
 	
    if ($first_char eq '@')
    {
        $phrase = substr($_, 2, length($phrase)-1);
    }
    elsif ($first_char eq '$')
    {
        my $sense = substr($_, 2, length($_));
        chomp($sense);
        if ($sense eq "literally") { $isLiterally = 1; }
        else { $isLiterally = 0; }
    }
    else
    {
        $tempPhrase = $phrase;
        if ($isLiterally == 1) 
        {
            $tempPhrase =~ s/\s+/_/g;
            if (hasWordnetSense($tempPhrase)) { 
                # if the phrase has a sense use that sense.
                $phrase = tempPhrase; 
            }
        } 
        
    }
}

my $relatedness = $lesk->getRelatedness("car#n#1", "bus#n#2");

($error, $errorString) = $lesk->getError();

die "$errorString\n" if($error);

#print "car (sense 1) <-> bus (sense 2) = $relatedness\n";

# ------------
# Subroutines
# ------------
sub hasWordnetSense
{
   $p = $_[0];
   my @words = split(/_/, $p);
   if ($words[0] eq "a") {
        $p = substr($p, 2);
   }
   return $wn->querySense($p);
}