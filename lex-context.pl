# ------------------------------------------------------
# Relatedness is computed with the vector metric
# Output: data/context
# To run: perl lex-context.pl train/test phrase_number
# ------------------------------------------------------

use WordNet::QueryData;
use WordNet::Similarity::vector; #other measurement
use List::Util 'max';

$type = $ARGV[0];
$phrase_number = $ARGV[1];


open INPUT, "data/phrase-topics/phrases/senses-$type/$phrase_number.txt" or die $!;
open OUT, ">data/lex/context-$type/$phrase_number.txt" or die $!;


my @lines = <INPUT>;
my $wn = WordNet::QueryData->new();
my $vector = WordNet::Similarity::vector->new($wn);

my $phrase;
my $previous_phrase = "";
my $isFigurative;

my %literal_dictionary = ();
my %figurative_dictionary = ();
my @contexts = ();
my @senses = ();
my %stopWords = ();
my %dictionary = ();

my $sense;
readStopWords();

my $count = 1;
foreach my $line (@lines)
{
    my $first_char = substr($line, 0, 1);
 	my $word;
   
    if ($first_char eq '@')
    {   
        $phrase = substr($line, 2);
        chomp($phrase);
        if ($previous_phrase ne "")
        {
            # process data
            # reset variables
            #computeContextRelatedness(@contexts);
            my $size = keys( %dictionary );

            if ($size > 1) {
                #print "\n$count $previous_phrase\n";
                print "Count: $count\n";
                $count = $count + 1;
                
                computeDictionaryRelatedness();
            }
            else
            {
                 print OUT "$isFigurative 0\n";
                OUT->autoflush(1);
            }
            @contexts = ();
            %dictionary = ();
            @senses = ();
            $previous_phrase = $phrase;
        }
        else 
        {
            $previous_phrase = $phrase;
        }
    }
    elsif ($first_char eq '$')
    {
        $sense = substr($line, 2, length($line));
        chomp($sense);
       
        if ($sense eq "figuratively") 
        { 
            $isFigurative = 1; 
        }
        else {
            $isFigurative = 0; 
        }
        push(@senses, $isFigurative); # Add what sense this context is
    }
    elsif (length($line) > 1) # ignore blank lines
    {
        chomp($line);
        @words = split(/ /, $line);
        push(@contexts, [@words]);    
        addWords(\%dictionary, @words);   
    }
}

my $size = keys( %dictionary );
if ($size > 1) {
    computeDictionaryRelatedness();
}

# Adds words to either the figurative or literal dictionary of a particular phrase.
sub addWords
{
    my ($dictionary_ref, @words) = @_;
    my $index = 0;
    
    foreach my $w (@words) 
    {
        chomp($w);
        my @elements = split(/#/, $w);
        
        if(not(isStopWord($elements[0])))
        {
            
            if ($#elements + 1 == 3)
            {     
                my $value = lc($w);
                $dictionary_ref->{$index} = $value;
                $index = $index + 1;
           }
       }
    }
}

# Computes relatedness within words from a context.
sub computeDictionaryRelatedness
{
    print "Computing relatedness...\n";
    my $maxkey = keys %dictionary;
    my @words = @dictionary{0 .. ($maxkey - 1)};
    #print "$size\n";
    my $index = 1;
    my $normalizeFactor = 0;
    my $totalRelatedness = 0;
    
    my $size = @words;

    foreach my $word (@words)
    {
        my $i = $index;
        while ($i < $size)
        {
            my $relatedness = getRelatedness($word, $words[$i]);
            $totalRelatedness = $totalRelatedness + $relatedness;
            $normalizeFactor = $normalizeFactor + 1;
            $i = $i + 1;
        }
       $index = $index + 1;
    }

    
    $totalRelatedness = ($totalRelatedness * 1.0) / $normalizeFactor;
    print "$isFigurative $totalRelatedness\n";
    if ($isFigurative == 1)
    {
        print OUT "$isFigurative $totalRelatedness\n";
    }
    else 
    { 
        print OUT "$isFigurative $totalRelatedness\n";
    }
    OUT->autoflush(1);
}

sub computeContextRelatedness
{
    print "Computing context relatedness...\n";
    foreach my $context (@contexts) #context is an array of arrays
    {
       my @words = @$context;
       my $sense = $senses[$i];
       my $totalRelatedness = 0;
       my $totalWords = 0;
       
       # Compute relatedness of each word to the words in the dictionary
       foreach my $word (@words)
       {
            my @elements = split(/#/, $word);
        
            if (not(isStopWord($elements[0])) and $#elements + 1 == 3) 
            {
                # get overall score of how related a word is to the other ones in the dictionary
                my $relatedness = 0;
                if ($sense == 1) #is figurative
                {
                    # Compare each word from the context to each of the words in the figurative dictionary.
                    $relatedness = getWordToContextRelatedness($word, %figurative_dictionary);
                }
                else
                {
                    $relatedness = getWordToContextRelatedness($word, %literal_dictionary);
                }
                $totalRelatedness = $totalRelatedness + $relatedness;
                $totalWords = $totalWords + 1;
            }
       }
       
       # Write the relatedness of that context (normalized by the number of words of that context):
       $totalRelatedness = ($totalRelatedness * 1.0) / $totalWords;
       print "$sense $totalRelatedness\n";
       print OUT "$totalRelatedness\n";
       OUT->autoflush(1);
    }
}

# Returns the relatedness of a word to other words from a context.
sub getWordToContextRelatedness
{
    my ($word, %dictionary) = @_;
    my $numWords = 0;
    my $totalRelatedness = 0;
    
    foreach my $key (keys(%dictionary))
    {
        #print "$key\n";
        my $relatedness = getRelatedness($word, $key);
        $totalRelatedness = $totalRelatedness + $relatedness;
        $numWords = $numWords + 1;
    }
    # Normalize the total relatedness by the number of words in the dictionary.
    $totalRelatedness = ($totalRelatedness * 1.0) / $numWords;
    return $totalRelatedness;
}

# Returns the relatedness between two words.
sub getRelatedness
{
    my $word1 = $_[0];
    my $word2 = $_[1];
    #print "$word1 $word2\n";
    chomp($word1);
    chomp($word2);
    
    my $relatedness = $vector->getRelatedness($word1, $word2);
    ($error, $errorString) = $vector->getError();
    die "$errorString\n" if($error);    
    
    return $relatedness;
}

# Reads the list of stopwords from a file.
sub readStopWords
{
    open STOP, "data/stopwords.english" or die $!;
    my @lines = <STOP>;
    
    for my $line (@lines)
    {
        chomp($line);
        $stopWords{lc($line)} = 1;
    }
    close STOP;
}

# Checks if a given word is a stopword.
sub isStopWord
{
    my $word = $_[0];
    return exists $stopWords{lc($word)};
}

close INPUT;
close OUT;
