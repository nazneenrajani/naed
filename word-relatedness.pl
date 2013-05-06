# ----------------------------------------------------------------------------
# Computes the relatedness of words from a phrase to the words from the context
# Vector similarity
# ---------------------------------------------------------------------------


use WordNet::QueryData;
use WordNet::Similarity::lesk;
use WordNet::SenseRelate::WordToSet;

 use WordNet::Similarity::vector; #other measurement


my $wn = WordNet::QueryData->new();
my $lesk = WordNet::Similarity::lesk->new($wn);

my $vector = WordNet::Similarity::vector->new($wn);

# To compute the sense of the words of a given phrase.  
=pod 
my %options = (wordnet => $wn,
                      measure => 'WordNet::Similarity::lesk');#'WordNet::Similarity::lesk');

my $wsd = WordNet::SenseRelate::WordToSet->new (%options);
=cut

my %dictionary = (); # Dictionary of the words present in the texts
my $totalWords = 0;
my $totalContexts = 0;

my @vectors = (); # Array that contains vectors of each sample context


open INFILE, "data/senses/temporal-train.txt" or die $!;
open FILE, "data/phrase-senses/temporal-train.txt" or die $!; # File that contains the phrase and the sense of the words.
#open INFILE, "edaena.txt" or die $!;
#open FILE, "edaena-phrases.txt" or die $!; # File that contains the phrase and the sense of the words.
my @lines = <INFILE>;
my @phrases = <FILE>; # Phrases or words from phrases with their sense.
#open OUT, ">data/vectors/2-train-vectors.txt" or die $!;
#open OUT, ">data/vectors/train-vectors.txt" or die $!;
open OUT, ">data/basic/temporal-train.txt";

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
    my $size = @phraseWords;
    
    my $numWords = 0;
    foreach my $w (@words) {
        if ($w ne "") { $numWords = $numWords + 1; }
    }
    foreach my $pw (@phraseWords) {
        chomp($pw);
        if ($pw ne "") { $size = $size + 1; }
    }
    my $normalizeFactor = $size * $numWords * 1.0;
    my $totalRelatedness = 0;
    my $maxValue = 0;
    foreach my $pw (@phraseWords) 
    {
        chomp($pw);
        if ($pw ne "") 
        {
            #print $pw . "\n";
            foreach my $w (@words) 
            {
                if ($w ne "") 
                {
                    my @word = split('#', $w);
                    my $length = @word;
                    if ($length == 3) 
                    {
                        my $relatedness = getRelatedness($pw, $w);
                        #if ($relatedness > $maxValue) { $maxValue = $relatedness;}
                        $totalRelatedness = $totalRelatedness + $relatedness;             
                    }           
                } 
            }
        } #if 
    } # foreach
    #my $normalizedRelatedness = ($totalRelatedness / $maxValue) / $normalizeFactor; # for lesk
    $normalizedRelatedness = $totalRelatedness / $normalizeFactor;
    print "$normalizedRelatedness\n";
    # figurative = 1
    my $result = "";
    if ($isLiterally == 1) { $result = $result . "0"; }
    else { $result = $result . "1"; }
    
    #print "$result $total\n";
    
    #---------
    #print OUT "$result $total\n";
    #OUT->autoflush(1);
    #----------
    
    #print 
    $totalRelatedness = $totalRelatedness / $normalizeFactor;

    $result = $result . " " . $normalizedRelatedness;
    print OUT "$result\n";
    OUT->autoflush(1);
    
    #print "$result\n";
    
    print "Done.\n";
    
    #print OUT "$result\n";
    #OUT->autoflush(1);

}

sub getRelatedness
{
    
    my $word1 = $_[0];
    my $word2 = $_[1];
    chomp($word1);
    chomp($word2);

=pod    
    my $relatedness = $lesk->getRelatedness($word1, $word2);
    ($error, $errorString) = $lesk->getError();
    die "$errorString\n" if($error);
=cut

#=pod
     my $relatedness = $vector->getRelatedness($word1, $word2);

  ($error, $errorString) = $vector->getError();

  die "$errorString\n" if($error);    
#=cut
    
    return $relatedness;
}

close INFILE;
close FILE;
close OUT;


