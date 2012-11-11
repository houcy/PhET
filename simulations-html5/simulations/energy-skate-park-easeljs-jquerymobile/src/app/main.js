require( [
             'websocket-refresh',
             'skater',
             'view/control-panel',
             'view/background',
             'spline',
             'model/physics',
             'view/easel-util',
             'view/pie-chart',
             'view/grid',
             'view/bar-chart',
             'view/speedometer'
         ], function ( WebsocketRefresh, Skater, ControlPanel, Background, Spline, Physics, EaselUtil, PieChart, Grid, BarChart, Speedometer ) {

    console.log( EaselUtil );
    WebsocketRefresh.listenForRefresh();

    console.log( location.href );

//    EaselUtil.changeHitDetection();

    var root = new createjs.Container();
    var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
    fpsText.x = 4;
    fpsText.y = 280;
    var groundHeight = 116;

    var skater = Skater.create( groundHeight );
    skater.groundY = 768 - groundHeight;

    //Cache the background into a single image
//        background.cache( 0, 0, 1024, 768, 1 );

    var splineLayer = Spline.createSplineLayer( groundHeight );

    root.addChild( Background.createBackground( groundHeight ) );
    var grid = new Grid( skater.groundY );
    grid.visible = false;
    root.addChild( grid );
    root.addChild( splineLayer );
    var barChart = BarChart.createBarChart( skater );
    barChart.x = 50;
    barChart.y = 50;
    barChart.visible = false;
    root.addChild( barChart );

    root.addChild( skater );
    root.addChild( fpsText );
    var pieChart = new PieChart( skater );
    pieChart.visible = false;
    root.addChild( pieChart );

    var speedometer = Speedometer.createSpeedometer( skater );
    speedometer.visible = false;
    root.addChild( speedometer );

    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    var canvas = document.getElementById( "c" );
    canvas.onselectstart = function () { return false; }; // IE
    canvas.onmousedown = function () { return false; }; // Mozilla

    var stage = new createjs.Stage( canvas );
    stage.mouseMoveOutside = true;
    stage.addChild( root );

    var frameCount = 0;

    var filterStrength = 20;
    var frameTime = 0, lastLoop = new Date, thisLoop;

    //Wire up the pie chart check box button to the visibility of the pie chart
    $( "#checkbox1" ).click( function () { barChart.visible = $( "#checkbox1" ).is( ":checked" ); } );
    $( "#checkbox2" ).click( function () { pieChart.visible = $( "#checkbox2" ).is( ":checked" ); } );
    $( "#checkbox3" ).click( function () { grid.visible = $( "#checkbox3" ).is( ":checked" ); } );
    $( "#checkbox4" ).click( function () { speedometer.visible = $( "#checkbox4" ).is( ":checked" ); } );

    function updateFrameRate() {
        frameCount++;

        //Get frame rate but filter transients: http://stackoverflow.com/questions/4787431/check-fps-in-js
        var thisFrameTime = (thisLoop = new Date) - lastLoop;
        frameTime += (thisFrameTime - frameTime) / filterStrength;
        lastLoop = thisLoop;
        if ( frameCount > 30 ) {
            fpsText.text = (1000 / frameTime).toFixed( 1 ) + " fps";// @"+location.href;
        }
    }

    //http://stackoverflow.com/questions/4009524/change-button-text-jquery-mobile
    (function ( $ ) {
        /*
         * Changes the displayed text for a jquery mobile button.
         * Encapsulates the idiosyncracies of how jquery re-arranges the DOM
         * to display a button for either an <a> link or <input type="button">
         */
        $.fn.changeButtonText = function ( newText ) {
            return this.each( function () {
                $this = $( this );
                if ( $this.is( 'a' ) ) {
                    $( 'span.ui-btn-text', $this ).text( newText );
                    return;
                }
                if ( $this.is( 'input' ) ) {
                    $this.val( newText );
                    // go up the tree
                    var ctx = $this.closest( '.ui-btn' );
                    $( 'span.ui-btn-text', ctx ).text( newText );
                    return;
                }
            } );
        };
    })( jQuery );

    $( '#barGraphLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="barChartIconImage" src="resources/barChartIcon.png" />' );
    $( '#pieChartLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/pieChartIcon.png" />' );
    $( '#gridLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/gridIcon.png" />' );
    $( '#speedLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/speedIcon.png" />' );

    var onResize = function () {
        var winW = $( window ).width(),
                winH = $( window ).height(),
                scale = Math.min( winW / 1024, winH / 768 ),
                canvasW = scale * 1024,
                canvasH = scale * 768;
        var canvas = $( '#c' );
        canvas.attr( 'width', canvasW );
        canvas.attr( 'height', canvasH );
        var left = (winW - canvasW) / 2;
        var top = (winH - canvasH) / 2;
        canvas.offset( {left: left, top: top} );
        root.scaleX = root.scaleY = scale;
        stage.update();

        $( "#navBar" ).css( 'top', top + 'px' ).css( 'left', (left + 50) + 'px' ).css( 'width', (canvasW - 100) + 'px' );

        //Scale the control panel up and down using css 2d transform
//        $( "#controlPanel" ).css( "-webkit-transform", "scale(" + scale + "," + scale + ")" );

        var controlPanel = $( '#controlPanel' );
        var rightOfControlPanel = canvasW + left;
//        controlPanel.css( 'width', canvasW * 0.3 + 'px' );
        controlPanel.css( 'width', '200px' );
        controlPanel.css( 'top', (top + 30) + 'px' );
        controlPanel.css( 'right', left + 'px' );


        //Apply css overrides last (i.e. after other css takes effect.
        //There must be a better way to do this, hopefully this can be improved easily.
        $( "#barGraphLabel .ui-btn-inner .ui-btn-text" ).text( "Bar Chart" );
        $( ".ui-shadow-inset" ).remove();
        var slider = $( ".ui-slider" );
        slider.css( "width", "100%" );
        slider.css( "marginTop", "0px" );
        slider.css( "marginLeft", "0px" );
        slider.css( "marginBottom", "0px" );
        slider.css( "marginRight", "0px" );

        //TODO: this vertical alignment is a hack that won't work for different settings
        $( '#barGraphLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
        $( '#pieChartLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
        $( '#gridLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
        $( '#speedLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );

//        $( "#controlPanel" ).css( "width", "100%" );
//        .
//                css( "top", top ).css( "right", right );
//        <span class="ui-btn-inner ui-corner-top"><span class="ui-btn-text">Bar Chart</span><span class="ui-icon ui-icon-checkbox-off ui-icon-shadow ui-iconsize-18">&nbsp;</span></span>
    };
    $( window ).resize( onResize );
    onResize(); // initial position

    createjs.Ticker.setFPS( 60 );
    createjs.Ticker.addListener( function () {
        Physics.updatePhysics( skater, groundHeight, splineLayer );
        updateFrameRate();
        pieChart.tick();
        stage.tick();
    } );

    //Enable touch and prevent default
    createjs.Touch.enable( stage, false, false );

    //Necessary to enable MouseOver events
    stage.enableMouseOver();

    //Paint once after initialization
    stage.update();

//    var $newCanvas = $( '<canvas id="newCanvasCreated"/>' ).width( 100 ).height( 1000 );
//    $( '#theMainBody' ).append( $newCanvas );
//    $( '#newCanvasCreated' ).css( 'border', '1px' ).draggable();
} );