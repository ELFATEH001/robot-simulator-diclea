package robotsimulator.ui;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import robotsimulator.cleaning.NettoyeurComplet;
import robotsimulator.cleaning.NettoyeurLibre;
import robotsimulator.cleaning.NettoyeurSauteurs;
import robotsimulator.cleaning.NettoyeurToutDroit;
import robotsimulator.cleaning.SmartCleaner;
import robotsimulator.model.Robot;
import robotsimulator.pollution.PollueurLibre;
import robotsimulator.pollution.PollueurSauteurs;
import robotsimulator.pollution.PollueurToutDroit;

/**
 * Factory class for creating unique visual representations (spirits) for different robot types.
 * Each robot type gets its own distinctive visual style.
 */
public class RobotVisualFactory {
    
    /**
     * Create the appropriate visual representation for a robot based on its type
     */
    public static Group createVisualSpirit(Robot robot) {
        double radius = robot.getRadius();
        Color robotColor = robot.getColor();
        
        Group spirit;
        
        // Determine robot type and create appropriate "spirit"
        if (robot instanceof PollueurToutDroit) {
            spirit = createStraightPolluterSpirit(radius, robotColor);
        } else if (robot instanceof PollueurSauteurs) {
            spirit = createJumpingPolluterSpirit(radius, robotColor);
        } else if (robot instanceof PollueurLibre) {
            spirit = createFreePolluterSpirit(radius, robotColor);
        } else if (robot instanceof SmartCleaner) {
            spirit = createSmartCleanerSpirit(radius, robotColor);
        } else if (robot instanceof NettoyeurToutDroit) {
            spirit = createStraightCleanerSpirit(radius, robotColor);
        } else if (robot instanceof NettoyeurSauteurs) {
            spirit = createJumpingCleanerSpirit(radius, robotColor);
        } else if (robot instanceof NettoyeurLibre) {
            spirit = createFreeCleanerSpirit(radius, robotColor);
        } else if (robot instanceof NettoyeurComplet) {
            spirit = createCompleteCleanerSpirit(radius, robotColor);
        } else {
            // Standard robot - simple circle with eye
            spirit = createStandardRobotSpirit(radius, robotColor);
        }
        
        // Bind position to robot
        spirit.translateXProperty().bind(robot.xProperty());
        spirit.translateYProperty().bind(robot.yProperty());
        
        return spirit;
    }
    
    /**
     * Standard Robot - Classic Circle with Eye
     */
    private static Group createStandardRobotSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        Circle body = new Circle(radius);
        body.setFill(color);
        body.setStroke(Color.DARKBLUE);
        body.setStrokeWidth(2);
        
        Circle eye = new Circle(radius * 0.3);
        eye.setFill(Color.WHITE);
        eye.setStroke(Color.BLACK);
        eye.setStrokeWidth(1);
        
        Circle pupil = new Circle(radius * 0.15);
        pupil.setFill(Color.BLACK);
        
        spirit.getChildren().addAll(body, eye, pupil);
        return spirit;
    }
    
    /**
     * Straight Polluter - Arrow pointing up (pollution rising)
     */
    private static Group createStraightPolluterSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Main arrow body pointing DOWN
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(
            0.0, radius,            // Bottom point (arrow head)
            -radius * 0.6, 0.0,     // Left middle
            -radius * 0.3, 0.0,     // Left inner
            -radius * 0.3, -radius, // Left top
            radius * 0.3, -radius,  // Right top
            radius * 0.3, 0.0,      // Right inner
            radius * 0.6, 0.0       // Right middle
        );
        arrow.setFill(color);
        arrow.setStroke(Color.DARKRED);
        arrow.setStrokeWidth(2);
        
        // Update toxic drops to be falling (below the arrow)
        Circle drop1 = new Circle(radius * 0.2);
        drop1.setTranslateY(radius * 1.3);  // Positive Y = below
        drop1.setFill(color.darker());
        drop1.setOpacity(0.7);
        
        Circle drop2 = new Circle(radius * 0.15);
        drop2.setTranslateY(radius * 1.6);  // Positive Y = below
        drop2.setTranslateX(radius * 0.3);
        drop2.setFill(color.darker());
        drop2.setOpacity(0.5);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(3);
        arrow.setEffect(shadow);
        
        spirit.getChildren().addAll(arrow, drop1, drop2);
        return spirit;
    }
    
    /**
     * Jumping Polluter - Star with motion trails
     */
    private static Group createJumpingPolluterSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Motion trail lines to show jumping
        for (int i = 0; i < 3; i++) {
            Line trail = new Line(-radius - i * 5, 0, -radius * 1.5 - i * 5, 0);
            trail.setStroke(color.deriveColor(0, 1, 1, 0.4 - i * 0.1));
            trail.setStrokeWidth(3);
            spirit.getChildren().add(trail);
        }
        
        // Create star shape
        Polygon star = createStar(radius, 6);
        star.setFill(color);
        star.setStroke(Color.DARKORANGE);
        star.setStrokeWidth(2);
        
        Glow glow = new Glow(0.7);
        star.setEffect(glow);
        
        spirit.getChildren().add(star);
        return spirit;
    }
    
    /**
     * Free Polluter - Chaotic cloud/blob shape
     */
    private static Group createFreePolluterSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Create irregular blob using multiple overlapping circles
        double[] angles = {0, Math.PI/3, 2*Math.PI/3, Math.PI, 4*Math.PI/3, 5*Math.PI/3};
        for (int i = 0; i < 6; i++) {
            double angle = angles[i];
            double distance = radius * 0.5;
            double size = radius * 0.6;
            
            Circle blob = new Circle(size);
            blob.setTranslateX(Math.cos(angle) * distance);
            blob.setTranslateY(Math.sin(angle) * distance);
            blob.setFill(color.deriveColor(0, 1, 1, 0.6));
            blob.setStroke(color.darker());
            blob.setStrokeWidth(1);
            spirit.getChildren().add(blob);
        }
        
        // Central core
        Circle core = new Circle(radius * 0.5);
        core.setFill(color);
        core.setStroke(Color.DARKRED);
        core.setStrokeWidth(2);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(color.darker());
        shadow.setRadius(5);
        core.setEffect(shadow);
        
        spirit.getChildren().add(core);
        return spirit;
    }
    
    /**
     * Smart Cleaner - Brain/gear shape (intelligence)
     */
    private static Group createSmartCleanerSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Intelligence "rays" emanating from center
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            Line ray = new Line(
                Math.cos(angle) * radius * 0.5, 
                Math.sin(angle) * radius * 0.5,
                Math.cos(angle) * radius * 0.8, 
                Math.sin(angle) * radius * 0.8
            );
            ray.setStroke(Color.CYAN);
            ray.setStrokeWidth(2);
            ray.setOpacity(0.8);
            spirit.getChildren().add(ray);
        }
        
        // Create gear shape
        Polygon gear = createGear(radius * 0.9, 8);
        gear.setFill(color);
        gear.setStroke(Color.DARKGREEN);
        gear.setStrokeWidth(2);
        
        // Brain symbol (glowing center)
        Circle center = new Circle(radius * 0.4);
        center.setFill(Color.LIGHTCYAN);
        center.setStroke(Color.DARKBLUE);
        center.setStrokeWidth(2);
        
        Glow glow = new Glow(0.6);
        center.setEffect(glow);
        
        spirit.getChildren().addAll(gear, center);
        return spirit;
    }
    
    /**
     * Straight Cleaner - Downward arrow with sparkles (cleaning downward)
     */
    private static Group createStraightCleanerSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Downward pointing arrow
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(
            0.0, radius,            // Bottom point (arrow head)
            radius * 0.6, 0.0,      // Right middle
            radius * 0.3, 0.0,      // Right inner
            radius * 0.3, -radius,  // Right top
            -radius * 0.3, -radius, // Left top
            -radius * 0.3, 0.0,     // Left inner
            -radius * 0.6, 0.0      // Left middle
        );
        arrow.setFill(color);
        arrow.setStroke(Color.DARKGREEN);
        arrow.setStrokeWidth(2);
        
        // Sparkles indicating cleaning
        Circle sparkle1 = new Circle(radius * 0.15);
        sparkle1.setTranslateX(-radius * 0.7);
        sparkle1.setTranslateY(radius * 0.5);
        sparkle1.setFill(Color.YELLOW);
        sparkle1.setStroke(Color.WHITE);
        
        Circle sparkle2 = new Circle(radius * 0.15);
        sparkle2.setTranslateX(radius * 0.7);
        sparkle2.setTranslateY(radius * 0.5);
        sparkle2.setFill(Color.YELLOW);
        sparkle2.setStroke(Color.WHITE);
        
        Circle sparkle3 = new Circle(radius * 0.12);
        sparkle3.setTranslateY(radius * 1.2);
        sparkle3.setFill(Color.WHITE);
        
        spirit.getChildren().addAll(arrow, sparkle1, sparkle2, sparkle3);
        return spirit;
    }
    
    /**
     * Jumping Cleaner - Diamond with energy bursts
     */
    private static Group createJumpingCleanerSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Energy burst lines
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            Line burst = new Line(
                Math.cos(angle) * radius * 0.6, 
                Math.sin(angle) * radius * 0.6,
                Math.cos(angle) * radius * 1.1, 
                Math.sin(angle) * radius * 1.1
            );
            burst.setStroke(Color.LIGHTGREEN);
            burst.setStrokeWidth(2);
            burst.setOpacity(0.7);
            spirit.getChildren().add(burst);
        }
        
        // Diamond shape
        Polygon diamond = new Polygon();
        diamond.getPoints().addAll(
            0.0, -radius * 0.9,
            radius * 0.9, 0.0,
            0.0, radius * 0.9,
            -radius * 0.9, 0.0
        );
        diamond.setFill(color);
        diamond.setStroke(Color.DARKGREEN);
        diamond.setStrokeWidth(2);
        
        Glow glow = new Glow(0.5);
        diamond.setEffect(glow);
        
        spirit.getChildren().add(diamond);
        return spirit;
    }
    
    /**
     * Free Cleaner - Rotating flower/fan shape
     */
    private static Group createFreeCleanerSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Create flower petals
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;
            Ellipse petal = new Ellipse(radius * 0.4, radius * 0.8);
            petal.setFill(color.deriveColor(0, 1.2, 1, 0.8));
            petal.setStroke(Color.DARKGREEN);
            petal.setStrokeWidth(1.5);
            petal.setRotate(Math.toDegrees(angle));
            petal.setTranslateX(Math.cos(angle) * radius * 0.3);
            petal.setTranslateY(Math.sin(angle) * radius * 0.3);
            spirit.getChildren().add(petal);
        }
        
        // Center circle
        Circle center = new Circle(radius * 0.4);
        center.setFill(Color.YELLOW);
        center.setStroke(Color.ORANGE);
        center.setStrokeWidth(2);
        
        // Add small white highlight
        Circle highlight = new Circle(radius * 0.15);
        highlight.setTranslateX(-radius * 0.1);
        highlight.setTranslateY(-radius * 0.1);
        highlight.setFill(Color.WHITE);
        highlight.setOpacity(0.7);
        
        spirit.getChildren().addAll(center, highlight);
        return spirit;
    }
    
    /**
     * Complete Cleaner - Shield/badge shape (comprehensive protection)
     */
    private static Group createCompleteCleanerSpirit(double radius, Color color) {
        Group spirit = new Group();
        
        // Shield shape
        Path shield = new Path();
        shield.getElements().addAll(
            new MoveTo(0, -radius),
            new LineTo(radius * 0.8, -radius * 0.5),
            new LineTo(radius * 0.8, radius * 0.3),
            new QuadCurveTo(radius * 0.5, radius * 1.2, 0, radius * 1.3),
            new QuadCurveTo(-radius * 0.5, radius * 1.2, -radius * 0.8, radius * 0.3),
            new LineTo(-radius * 0.8, -radius * 0.5),
            new ClosePath()
        );
        shield.setFill(color);
        shield.setStroke(Color.DARKGREEN);
        shield.setStrokeWidth(3);
        
        // Checkmark in center
        Path check = new Path();
        check.getElements().addAll(
            new MoveTo(-radius * 0.3, 0),
            new LineTo(-radius * 0.1, radius * 0.3),
            new LineTo(radius * 0.4, -radius * 0.4)
        );
        check.setStroke(Color.WHITE);
        check.setStrokeWidth(3.5);
        check.setStrokeLineCap(StrokeLineCap.ROUND);
        check.setStrokeLineJoin(StrokeLineJoin.ROUND);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GREEN);
        shadow.setRadius(8);
        shield.setEffect(shadow);
        
        spirit.getChildren().addAll(shield, check);
        return spirit;
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Helper: Create star polygon
     */
    private static Polygon createStar(double radius, int points) {
        Polygon star = new Polygon();
        double outerRadius = radius;
        double innerRadius = radius * 0.4;
        
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.PI / points * i - Math.PI / 2; // Start from top
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            star.getPoints().addAll(
                Math.cos(angle) * r,
                Math.sin(angle) * r
            );
        }
        return star;
    }
    
    /**
     * Helper: Create gear polygon
     */
    private static Polygon createGear(double radius, int teeth) {
        Polygon gear = new Polygon();
        double outerRadius = radius;
        double innerRadius = radius * 0.7;
        
        for (int i = 0; i < teeth * 4; i++) {
            double angle = 2 * Math.PI / (teeth * 4) * i;
            double r = (i % 4 == 0 || i % 4 == 1) ? outerRadius : innerRadius;
            gear.getPoints().addAll(
                Math.cos(angle) * r,
                Math.sin(angle) * r
            );
        }
        return gear;
    }
}