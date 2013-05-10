
val starts = """(?:[#@])|\b(?:http)"""
val stopwords = io.Source.fromFile("../data/stopwords.english").getLines.toSet
val input = io.Source.fromFile("../data/clean_train.txt").getLines.map{x => 
      x
      //.replaceAll("""([\?!()\";\|\[\].,':])""", " $1 ")
      //.trim
      .split("\\s+")
      .toIndexedSeq
      //.filterNot(x => x.startsWith(starts))
  }.map(y => y.filterNot(z =>stopwords.contains(z)).mkString(" "))
input.foreach(println)