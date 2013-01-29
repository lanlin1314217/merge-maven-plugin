# merge-maven-plugin
Maven(3) plugin which merges multiple files into one

## Configuration exmaple
<pre>
&lt;mergers&gt;
&nbsp;&nbsp;&lt;merge&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;target&gt;${build.outputDirectory}/target.txt&lt;/target&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;sources&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;source&gt;src/main/config/${property}/application.txt&lt;/source&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;source&gt;src/main/config/extended/application.txt&lt;/source&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;source&gt;src/main/config/default/application.txt&lt;/source&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/sources&gt;
&nbsp;&nbsp;&lt;/merge&gt;
&lt;/mergers&gt;
</pre>
