# ------------------------------------------------------------------
# Computes the relatedness of two elements and gives the lesk score.
# http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lesk.pm
# ------------------------------------------------------------------


use WordNet::QueryData;
use WordNet::Similarity::lesk;
use WordNet::SenseRelate::WordToSet;


my $wn = WordNet::QueryData->new();
my $lesk = WordNet::Similarity::lesk->new($wn);

# To compute the sense of the words of a given phrase.   
my %options = (wordnet => $wn,
                      measure => 'WordNet::Similarity::lesk');#'WordNet::Similarity::lesk');

my $wsd = WordNet::SenseRelate::WordToSet->new (%options);

my %dictionary = (); # Dictionary of the words present in the texts
my $totalWords = 0;
my $totalContexts = 0;

my @vectors = (); # Array that contains vectors of each sample context

open INFILE, "edaena.txt" or die $!;
my @lines = <INFILE>;

my $phrase = "";
my $isLiterally;


readDictionary(@lines); # Populate the dictionary of words present in the file.



foreach (@lines) {
    my $first_char = substr($_, 0, 1);
 	my $word;
 	
    if ($first_char eq '@')
    {   
        $phrase = substr($_, 2);
        chomp($phrase);
    }
    elsif ($first_char eq '$')
    {
        my $sense = substr($_, 2, length($_));
        chomp($sense);
        if ($sense eq "literally") { $isLiterally = 1; }
        else { $isLiterally = 0; }
    }
    elsif (length($_) > 1) # ignore blank lines
    {
        
        $tempPhrase = $phrase;
        my @context = split(/ /, $_); # Words that are going to be compared against the phrase

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

        getRelatednessVector(@context);
        
     } # else
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
   
   if ($words[0] eq "a") 
   {
        $p = substr($p, 2);
   }
   return $wn->querySense($p);
}

sub readDictionary
{
    @lines = @_;
    foreach (@lines) {
        my $first_char = substr($_, 0, 1);
 	my $word;
    
        if ($first_char ne '@' and $first_char ne '$' and (length($_) > 1) )
        {   
            $totalContexts = $totalContexts + 1;
            $phrase = substr($_, 2);
            chomp($phrase);
            
            $tempPhrase = $phrase;
            my @words = split(/ /, $_);
     
            foreach (@words) 
            {
                my @word = split(/#/, $_);
                my $word = lc($word[0]);
                chomp($word);
                if ($word ne "" and not (exists $dictionary{$word}) )
                {
                    $dictionary{$word} = $totalWords;
                    $totalWords = $totalWords + 1;
                }
            } 
         }

    }
}




sub getRelatednessVector
{
    my @phraseVectors = ();
    my @arr = 0 x $totalWords;
    my @phraseWords = split(/ /, $phrase);
    my @context;
    my $totalVectors = @phraseWords;
    
    
    
    #print "Getting relatedness vector:\n";
    my @words = @_;

    foreach (@words)
    {
        @word = split(/#/, $_);
        push(@context, $word[0]);
    }
    #print @context;
    #@hello = @context;
    #@context = ['cake', 'eat', 'tea'];
    my $phraseSense = "";
    foreach (@phraseWords) {
        my $pSense ="";
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
    
    chomp($phraseSense);
    print($phraseSense);
    
  


    #print @words;
    #print "\n";
    #print "phrase:  $p\n";
    #print @arr;
}

sub getRelatedness
{
    
}

=pod
print "$dictionary{$_} - $_\n" for keys %dictionary;

print $totalWords;
print "\n";
print length(%dictionary);
print "\n";
=cut


#print "\n";
@a = (1, 2, 3);
@keys = keys %dictionary;
$size = @keys;

=pod
print "Total contexts: $totalContexts\n";
print "Total words: $totalWords\n";
print "total words: $size\n";
print $size;
print "\n";
=cut

# if (exists $strings{$string}) {

