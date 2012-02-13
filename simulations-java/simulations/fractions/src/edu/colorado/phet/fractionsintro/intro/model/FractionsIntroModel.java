// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.ChosenRepresentation;

import static edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSet.fromContainerSetState;

/**
 * Model for the Fractions Intro sim.
 * <p/>
 * Property is a convenient interface for clients, but causes problems when mapping between multiple representations.
 * I should create an isolated case of this, because trivial cases seem like they should work.
 * One solution would be to have the Property.set() methods only be called from the user side, not from the model side.
 * When any of the client interface methods are called, the rest of the state should update to reflect the new state.
 * This means that all handlers in this class should call to the state itself, not to any of the derived properties.
 * <p/>
 * New style:
 * 1. No way to forget to call reset() on some representation instances
 * 2. Interface setters are all focused on "update the entire state to match the given request"
 *
 * @author Sam Reid
 */
public class FractionsIntroModel {
    private final Property<FractionsIntroModelState> state = new Property<FractionsIntroModelState>( new FractionsIntroModelState() );

    public final Clock clock = new ConstantDtClock();

    //Observable parts of the model
    public final SettableProperty<ChosenRepresentation> representation =
            new ClientProperty<ChosenRepresentation>( state, new Function1<FractionsIntroModelState, ChosenRepresentation>() {
                public ChosenRepresentation apply( FractionsIntroModelState s ) {
                    return s.representation;
                }
            }, new Function2<FractionsIntroModelState, ChosenRepresentation, FractionsIntroModelState>() {
                public FractionsIntroModelState apply( FractionsIntroModelState s, ChosenRepresentation representation ) {
                    return s.representation( representation );
                }
            }
            );

    public final IntegerProperty numerator =
            new IntClientProperty( state, new Function1<FractionsIntroModelState, Integer>() {
                public Integer apply( FractionsIntroModelState s ) {
                    return s.numerator;
                }
            }, new Function2<FractionsIntroModelState, Integer, FractionsIntroModelState>() {
                public FractionsIntroModelState apply( FractionsIntroModelState s, Integer numerator ) {
                    int oldValue = s.numerator;
                    int delta = numerator - oldValue;
                    if ( delta > 0 ) {
                        for ( int i = 0; i < delta; i++ ) {
                            final PieSet p = s.pieSet.animateBucketSliceToPie( s.containerSet.getFirstEmptyCell() );
                            s = s.pieSet( p ).containerSet( p.toContainerSet() ).numerator( numerator );
                        }
                    }
                    else if ( delta < 0 ) {
                        for ( int i = 0; i < Math.abs( delta ); i++ ) {
                            final PieSet p = s.pieSet.animateSliceToBucket( s.containerSet.getLastFullCell() );
                            s = s.pieSet( p ).containerSet( p.toContainerSet() ).numerator( numerator );
                        }
                    }
                    else {
                        //Nothing to do if delta == 0
                    }
                    return s;
                }
            }
            ).toIntegerProperty();

    public final IntegerProperty denominator =
            new IntClientProperty( state, new Function1<FractionsIntroModelState, Integer>() {
                public Integer apply( FractionsIntroModelState s ) {
                    return s.denominator;
                }
            },
                                   new Function2<FractionsIntroModelState, Integer, FractionsIntroModelState>() {
                                       public FractionsIntroModelState apply( FractionsIntroModelState s, Integer denominator ) {

                                           System.out.println( "FractionsIntroModel.apply.denominator changed old=" + s.denominator + ", new = " + denominator );
                                           //create a new container set
                                           ContainerSet cs = s.containerSet.denominator( denominator ).padAndTrim();
                                           System.out.println( "old state = " + s.containerSet );
                                           System.out.println( "new state = " + cs );
                                           System.out.println();
                                           return s.pieSet( fromContainerSetState( cs ) ).containerSet( cs ).denominator( denominator );
                                       }
                                   }
            ).toIntegerProperty();

    public final SettableProperty<ContainerSet> containerSet = new ClientProperty<ContainerSet>(
            state, new Function1<FractionsIntroModelState, ContainerSet>() {
        public ContainerSet apply( FractionsIntroModelState s ) {
            return s.containerSet;
        }
    },
            new Function2<FractionsIntroModelState, ContainerSet, FractionsIntroModelState>() {
                public FractionsIntroModelState apply( FractionsIntroModelState s, ContainerSet containerSet ) {
                    return s.containerSet( containerSet ).
                            pieSet( fromContainerSetState( containerSet ) ).
                            numerator( containerSet.numerator ).
                            denominator( containerSet.denominator );
                }
            }
    );

    //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
    public final SettableProperty<PieSet> pieSet = new ClientProperty<PieSet>(
            state, new Function1<FractionsIntroModelState, PieSet>() {
        public PieSet apply( FractionsIntroModelState s ) {
            return s.pieSet;
        }
    },
            new Function2<FractionsIntroModelState, PieSet, FractionsIntroModelState>() {
                public FractionsIntroModelState apply( FractionsIntroModelState s, PieSet pieSet ) {
                    final ContainerSet cs = pieSet.toContainerSet();
                    //Update both the pie set and container state to match the user specified pie set
                    return s.pieSet( pieSet ).containerSet( cs ).numerator( cs.numerator );
                }
            }
    );

    public FractionsIntroModel() {
//        numerator.valueEquals( 2 ).trace( "got a 2" );
//        containerSet.addObserver( new SimpleObserver() {
//            public void update() {
//                System.out.println( "New container state: " + containerSet.get() );
//            }
//        } );

//        numerator.trace( "Numerator" );
        //Animate the model when the clock ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                final FractionsIntroModelState s = state.get();
                final PieSet newPieSet = s.pieSet.stepInTime( clockEvent.getSimulationTimeChange() );
                state.set( s.pieSet( newPieSet ).containerSet( newPieSet.toContainerSet() ) );
            }
        } );
    }

    public void resetAll() {
        state.set( new FractionsIntroModelState() );
    }

    public Clock getClock() {
        return clock;
    }
}