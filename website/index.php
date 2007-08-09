<?php
    if (!defined('SITE_ROOT')) {
        define("SITE_ROOT", "./");
    }
    
    include_once("admin/sim-utils.php");
    include_once("admin/web-utils.php");
    
    /*

        TODO: 

            Reinsert <?xml version="1.0" encoding="UTF-8"?>    
                after IE6 is dead

    */

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>PhET :: Physics Education Technology at CU Boulder</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />
<style type="text/css">
/*<![CDATA[*/
        @import url(css/main.css);
        @import url(css/home.css);        
/*]]>*/
</style>
</head>

<body>
    <div id="skipNav">
        <a href="#content" accesskey="0">Skip to Main Content</a>
    </div>

    <div id="header">
        <div id="headerContainer">
            <div class="images">
                <span class="logo">
					<a href="../index.php"><img src="images/phet-logo.gif" alt="PhET Logo" title="Click here to go to the home page" /></a>
                </span>
                
                <span class="title">
                    <img src="images/logo-title.jpg" alt="Physics Education Technology - University of Colorado, Boulder" title="Physics Education Technology - University of Colorado, Boulder" />
                </span>
            </div>

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li  class="selected"><a href="index.php" accesskey="1">Home</a></li>

                    <li><a href="simulations/index.php?cat=Top_Simulations" accesskey="2">Simulations</a></li>

                    <li><a href="research/index.php" accesskey="3">Research</a></li>

                    <li><a href="about/index.php" accesskey="4">About PhET</a></li>
                </ul>
            </div>
            
            <div class="clear"></div>
        </div>
    </div>

    <div id="container">
        <div class="home-page">
            <div class="mainImage">    
                <a href="simulations/index.php"><img width="300" src="random-thumbnail.php" alt="Random screenshot of a simulation" title="Click here to view the simulations" /></a>
            </div>

            <h1>Interactive Physics Simulations</h1>

            <p class="openingParagraph">Fun, interactive simulations of physical phenomena from the Physics Education Technology project at the University of Colorado.</p>

            <p class="findOutMore" onclick="javascript:location.href='simulations/index.php?cat=Top_Simulations'">
                <a href="simulations/index.php?cat=Top_Simulations">
                    Play with sims... >
                </a>   
            </p>

            <div class="clear"></div>
        </div>

		<div id="home-page-sponsors">
			<div class="sponsor">
				<a href="http://www.nsf.gov/" title="Click here to visit the National Science Foundation" >
					<img src="images/nsf-logo.gif" height="28" alt="National Science Foundation Logo"/>
				</a>
				<p>
					<a href="http://www.nsf.gov/" title="Click here to visit the National Science Foundation" >
						The National Science Foundation
					</a>
				</p>
			</div>
		
			<div class="sponsor">
				<a href="http://www.hewlett.org/Default.htm" title="Click here to visit the William and Flora Hewlett Foundation">
					<img src="images/hewlett-logo.jpg" height="28" alt="William and Flora Hewlett Foundation Logo"/>
				</a>
				<p>
					<a href="http://www.hewlett.org/Default.htm" title="Click here to visit the William and Flora Hewlett Foundation">
						The William and Flora Hewlett Foundation
					</a>
				</p>
			</div>
			
			<div class="clear"></div>
		</div>
		
		<div class="clear"></div>

        <div class="practices">
            <dl>
                <dt onclick="javascript:location.href='get_phet/index.php'"><a href="get_phet/index.php">Run our Simulations</a></dt>

                <dd><a href="simulations/index.php">On Line</a></dd>

                <dd><a href="get_phet/full_install.php">Full Installation</a></dd>

                <dd><a href="get_phet/simlauncher.php">One at a Time</a></dd>
            </dl>

            <dl>
                <dt onclick="javascript:location.href='teacher_ideas/index.php'"><a href="teacher_ideas/index.php">Teacher Ideas &amp; Activities</a></dt>

                <dd><a href="teacher_ideas/browse.php">Search for lesson plans and activities that were created by teachers to use with the PhET simulations</a></dd>
            </dl>

            <dl>
                <dt onclick="javascript:location.href='contribute/index.php'"><a href="contribute/index.php">Contribute</a></dt>

                <dd><a href="teacher_ideas/index.php">Provide ideas you've used in class</a></dd>

                <dd><a href="contribute/index.php">Other contributions</a></dd>
            </dl>

            <dl class="last">
                <dt onclick="javascript:location.href='simulations/index.php'"><a href="simulations/index.php">Browse</a></dt>

                <dd>
	                <a class="nolink" href="simulations/index.php">
	                    <?php
	                        display_slideshow(sim_get_static_previews(), "150", "110");
	                    ?>
	                </a>
				</dd>

                <dd class="readMore"><a href="simulations/index.php"><img src="images/search.gif" alt="Search" title="Search" /></a></dd>
            </dl>

            <div class="clear">
                &nbsp;
            </div>
        </div>

        <div id="footer">
            <p>&copy; 2007 University of Colorado. All rights reserved.</p>
        </div>
        
    </div>
</body>
</html>
