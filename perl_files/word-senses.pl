# ------------------------------------------------------
# Reads a file with this format:
# @ phrase
# $ sense (figurative/literal)
# text
# and outputs a file containing the phrase, sense and the text with wordnet senses
#
# To run the program: perl word-sense.pl > yourfile.txt
# ------------------------------------------------------

use WordNet::Similarity::lesk;
use WordNet::QueryData;
use WordNet::SenseRelate::AllWords;
use WordNet::Tools;


open INFILE, "data/senses/train2-lex.txt" or die $!;
my @lines = <INFILE>;

my $querydata = WordNet::QueryData->new;
my $wntools = WordNet::Tools->new($querydata);    
my %options = (wordnet => $querydata,
                wntools => $wntools,
                measure => 'WordNet::Similarity::lesk'
                );

my $obj = WordNet::SenseRelate::AllWords->new(%options);


$number = 1;

foreach (@lines) {
    $first_char = substr($_, 0, 1);
 	    
    if ($first_char eq '@' or $first_char eq '$') 
    {
        print $_;
    }
    else 
    {
       
        my @context = split(/ /, $_);
        my $index = 0;
        my @wordnet_context;
        
        # Only use words that have word senses in WordNet
        foreach (@context) {
            $result = $querydata->querySense($_);
            if ($result) {
                $wordnet_context[$index] = $_;
                $index = $index + 1;
            }
        }
        my @res = $obj->disambiguate (window => 3,
                                      scheme => 'normal',
                                      tagged => 0,
                                      context => [@wordnet_context]);
        
        print join (' ', @res), "\n";
        $number = $number + 1;
    }
}

close INFILE;
