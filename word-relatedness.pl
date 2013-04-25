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


open INFILE, "data/senses/senses-train.txt" or die $!;
open FILE, "data/phrase-senses/phrases-train.txt" or die $!; # File that contains the phrase and the sense of the words.
#open INFILE, "edaena.txt" or die $!;
#open FILE, "edaena-phrases.txt" or die $!; # File that contains the phrase and the sense of the words.
my @lines = <INFILE>;
my @phrases = <FILE>; # Phrases or words from phrases with their sense.
open OUT, ">data/vectors/train-vectors.txt" or die $!;


my $phrase = "";
my $isLiterally;
my $count = 0;

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
        else { 
        $isLiterally = 0; }
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

        computeRelatednessVector(@context);
        
        $count = $count + 1;
        
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
                my $word = lc($word[0]); # lower case
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

# Receives as parameter the context words.
sub computeRelatednessVector
{
    print "Computing vector...\n";
    my @phraseVectors = ();
    my @phraseVector = (0) x $totalWords;
    #my @wrelatedness = 0 x $totalWords;
    
    my $pSense = $phrases[count]; # Get the corresponding phrase sense from the read file.
    my @phraseWords = split(/ /, $pSense);
    
    my @context;
    my $totalVectors = @phraseWords;
    
    #print "Getting relatedness vector:\n";
    my @words = @_;
    $size = @phraseWords;
    
    foreach my $pw (@phraseWords) {
        chomp($pw);
        if ($pw ne "") {
            my @wrelatedness = (0) x $totalWords; #initialize array with zeros.
            
            #print $pw . "\n";
            foreach my $w (@words) {
                if ($w ne "") {
                my @word = split('#', $w);
                my $length = @word;
                if ($length == 3) {
                    my $relatedness = getRelatedness($pw, $w);
                    # write relatedness in array
                    
                    my $index = $dictionary{lc($word[0])};
                    
                    if ($wrelatedness[$index] != 0) {
                        $wrelatedness[$index] = $relatedness / 2.0;
                    }
                    else { $wrelatedness[$index] = $relatedness; }
                    }
                }
                
            } # foreach
            foreach my $v (@wrelatedness) {
                #print $v . "\n";
            }
            push(@phraseVectors, [@wrelatedness]);
        } #if
        
    } # foreach

    $s = @phraseVectors;
    #print "$s\n";
    
    for $aref ( @phraseVectors ) {
        my $i = 0;

        #print "\t [ @$aref ],\n";
    }
    # Add vectors:
    
    for $aref ( @phraseVectors ) {
        my $i = 0; # initialize index of the phrase vector
        foreach my $v (@$aref) {
            $phraseVector[$i] = $phraseVector[$i] + $v;
            $i = $i + 1;
        }
    }
    # figurative = 1
    my $result = "";
    if ($isLiterally == 1) { $result = $result . "0"; }
    else { $result = $result . "1"; }
    
    #print 
    foreach my $p (@phraseVector) {
        $result = $result . " " . $p;
    }
    #print "$result\n";
    print "Done.\n";
    print OUT "$result\n";
    OUT->autoflush(1);

}

sub getRelatedness
{
    
    my $word1 = $_[0];
    my $word2 = $_[1];
    chomp($word1);
    chomp($word2);
    #print $word1 . " " . $word2 . "\n";
    my $relatedness = $lesk->getRelatedness($word1, $word2);
    ($error, $errorString) = $lesk->getError();
    die "$errorString\n" if($error);
    #print "$word1, $word2 = $relatedness\n";
    return $relatedness;
}

=pod
print "$dictionary{$_} - $_\n" for keys %dictionary;

print $totalWords;
print "\n";
print length(%dictionary);
print "\n";
=cut
#print "$dictionary{$_} - $_\n" for keys %dictionary;
#print "total words: $totalWords\n";

#print "\n";
@a = (1, 2, 3);
@keys = keys %dictionary;
$size = @keys;

close INFILE;
close FILE;

=pod
print "Total contexts: $totalContexts\n";
print "Total words: $totalWords\n";
print "total words: $size\n";
print $size;
print "\n";
=cut

# if (exists $strings{$string}) {

