open IN, "all/lex-train.txt" or die $!;
#open INFILE, "data/senses/senses-lex-train.txt" or die $!;
#open OUT, ">data/phrase-senses/edaena.txt" or die $!;
#open INFILE, "data/senses/edaena.txt" or die $!;
#open OUT, ">data/phrase-senses/edaena.txt" or die $!;
#open OUT, ">data/phrase-senses/l-train-bread-drop.txt" or die $!;


my @lines = <IN>;

my $n = 1;
my $count = 1;
my $type = "train";
my $file = "";
my $sense = "";


foreach my $line (@lines)
{
    chomp($line);
    # print $line;
    my $first_char = substr($line, 0, 1);
 
    if ($first_char eq '@')
    {   
        #$file = ">" . $n . "-train.txt";
        #open OUT, $file or die $!;
    }
    if ($first_char eq '$')
    {
        if ($count == 2) 
        {
            $n = $count;
            $count = 1;
        }
        $sense = substr($_, 2, length($_));
        chomp($sense);
        #if ($sense eq "literally") { $isLiterally = 1; }
        #else { $isLiterally = 0; }
        $file = ">" . $n . "-$sense.txt";
        open OUT, $file or die $!;
        
        $count = $count + 1;
    }
    elsif (length($line) > 1) # ignore blank lines
    {
        print OUT $line;
       # print $line;
    }

}


close IN;
