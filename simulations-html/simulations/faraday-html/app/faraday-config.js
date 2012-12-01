// Copyright 2002-2012, University of Colorado

/*
 * RequireJS configuration file for the "Faraday's Electromagnetic Lab" sim.
 * Paths are relative to the location of this file.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require.config( {

    deps: ["faraday-main"],

    paths: {
        easel:"../../../contrib/easel-0.5.0",
        i18n:"../../../contrib/i18n-2.0.1a",
        image:"../../../contrib/image-0.2.1",
        jquery:'../../../contrib/jquery-1.8.3',
        'jquery.mobile':'../../../contrib/jquery.mobile-1.2.0/jquery.mobile-1.2.0'
    },

    shim: {
        easel: {
            exports: "createjs"
        }
    },

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );