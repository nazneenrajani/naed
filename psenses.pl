# ------------------------------------------------------------------
# Computes the senses of a phrase based on the context words.
# http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lesk.pm
# ------------------------------------------------------------------


use WordNet::QueryData;
use WordNet::Similarity::lesk;
use WordNet::SenseRelate::WordToSet;


my $wn = WordNet::QueryData->new();

my $querydata = WordNet::QueryData->new;
my $lesk = WordNet::Similarity::lesk->new($wn);

# To compute the sense of the words of a given phrase.   
my %options = (wordnet => $wn,
                      measure => 'WordNet::Similarity::lesk');#'WordNet::Similarity::lesk');

my $wsd = WordNet::SenseRelate::WordToSet->new (%options);

my %englishStopWords = (); # Dictionary of English stop words
my %dictionary = (); # Dictionary of the words present in the texts
my $totalWords = 0;
my $totalContexts = 0;

my @vectors = (); # Array that contains vectors of each sample context

open INFILE, "data/senses/senses-train.txt" or die $!;

my @lines = <INFILE>;

my $phrase = "";


foreach (@lines) {
    my $first_char = substr($_, 0, 1);
 	my $word;
 	
    if ($first_char eq '@')
    {   
        $phrase = substr($_, 2);
        chomp($phrase);
        #print $phrase . "\n";
    }
    elsif ($first_char eq '$')
    {
        my $sense = substr($_, 2, length($_));
        chomp($sense);
        if ($sense eq "literally") { $isLiterally = 1; }
        else { $isLiterally = 0; }
        
        if ($isLiterally == 1) 
        {
            my @words = split(' ', $phrase);
            my $tempPhrase = $phrase;
            if ($words[0] eq "a") 
             {
                $tempPhrase = substr($phrase, 2);
             }
              $tempPhrase =~ s/\s+/_/g;
             if (hasWordnetSense($tempPhrase)) { 
                $phrase = $tempPhrase; # if the phrase has a sense use that sense.
            }
        }
    }
    elsif (length($_) > 1) # ignore blank lines
    {
        
        my @context = split(/ /, $_);
        my @words = (); # Words that are going to be compared against the phrase
     
        foreach (@context) {
            my @word = split(/#/, $_);
 	        push(@words, $_);
        }
        writeSense(@words);
     } # elseif

}

my $relatedness = $lesk->getRelatedness("car#n#1", "bus#n#2");

($error, $errorString) = $lesk->getError();

die "$errorString\n" if($error);


# ------------
# Subroutines
# ------------
sub hasWordnetSense
{
   $p = $_[0];
   my @words = split(/_/, $p);
   
   if ($words[0] eq "a") 
   {
        $p = substr($p, 2);
   }
   return $wn->querySense($p);
}


sub writeSense
{   
    my @phraseWords = split(/ /, $phrase);
    #my @context = @_;
    my $phraseSense = "";
    #print $phrase . "\n";
    my @words = @_;
    #print @phraseWords;
    my @context;
    foreach (@words)
    {
        @word = split(/#/, $_);
        push(@context, $word[0]);
    }

    foreach (@phraseWords) {
        my $pSense ="";
        $result = $querydata->querySense($_);
        if ($result) 
        {
            
            my $result = $wsd->disambiguate (target => $_,
                              context => [@context]);
            my $max = 0;
           foreach my $key (keys %$result) {
                my $r = $result->{$key};
                if ($r > $max) {
                    $max = $r;
                    $pSense = $key;
                }
               #print $key, ' : ', $result->{$key}, "\n";
           }
           $phraseSense = $phraseSense . $pSense . " ";
        }

    }

    print($phraseSense);
    print "\n";

}
