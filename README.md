This is the code accompanying [this blog post][blog] about writing a code
coverage tool. The code is more or less as described in the post, except
for some small things like a nicer command line interface.

Unlike the code in the post, this uses maven, so you should be
able to compile with

```bash
mvn compile
```

and run the instrumentation with

```bash
mvn exec:java -Dcoverage.report.path=baseline.lcov \
  -Dexec.mainClass="io.badawi.coverage.Main" -Dexec.args="-d instrumented <java-files>"
```

You need `lcov` and `genhtml` to work with the generated coverage reports.
These are pretty widespread and should be easy to get via whatever package
manager you use (I've tried `sudo apt-get install lcov` on Ubuntu and
`brew install lcov` on OS X).

[blog]: http://ismail.badawi.io/blog/2013/05/03/writing-a-code-coverage-tool/
