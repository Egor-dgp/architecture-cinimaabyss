package main

import (
	"encoding/json"
	"log"
	"math/rand"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"strconv"
	"time"
)

type Config struct {
	Port                 string
	MonolithURL          string
	MoviesServiceURL     string
	GradualMigration     bool
	MoviesMigrationPercent int
}

var config Config

func main() {
	// Load configuration
	config = loadConfig()

	// Seed random number generator
	rand.Seed(time.Now().UnixNano())

	// Set up HTTP routes
	http.HandleFunc("/health", healthHandler)
	http.HandleFunc("/api/movies", moviesHandler)
	http.HandleFunc("/api/users", usersHandler)
	http.HandleFunc("/api/payments", paymentsHandler)
	http.HandleFunc("/api/subscriptions", subscriptionsHandler)
	
	// Proxy other routes to monolith
	http.HandleFunc("/", proxyHandler)

	// Start server
	log.Printf("Starting proxy service on port %s", config.Port)
	log.Printf("Movies migration percent: %d%%", config.MoviesMigrationPercent)
	log.Fatal(http.ListenAndServe(":"+config.Port, nil))
}

func loadConfig() Config {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8000"
	}

	moviesMigrationPercent, err := strconv.Atoi(os.Getenv("MOVIES_MIGRATION_PERCENT"))
	if err != nil {
		moviesMigrationPercent = 0
	}

	gradualMigration := os.Getenv("GRADUAL_MIGRATION") == "true"

	return Config{
		Port:                 port,
		MonolithURL:          os.Getenv("MONOLITH_URL"),
		MoviesServiceURL:     os.Getenv("MOVIES_SERVICE_URL"),
		GradualMigration:     gradualMigration,
		MoviesMigrationPercent: moviesMigrationPercent,
	}
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]bool{"status": true})
}

func moviesHandler(w http.ResponseWriter, r *http.Request) {
	// Check if we should route to microservice based on migration percentage
	shouldRouteToMicroservice := false
	
	if config.GradualMigration {
		// Generate random number between 0 and 100
		randomPercent := rand.Intn(100)
		shouldRouteToMicroservice = randomPercent < config.MoviesMigrationPercent
		
		log.Printf("Movies request - Random: %d, Migration percent: %d, Route to microservice: %v", 
			randomPercent, config.MoviesMigrationPercent, shouldRouteToMicroservice)
	}

	var targetURL string
	if shouldRouteToMicroservice {
		targetURL = config.MoviesServiceURL
		log.Println("Routing movies request to microservice")
	} else {
		targetURL = config.MonolithURL
		log.Println("Routing movies request to monolith")
	}

	// Forward the request
	forwardRequest(w, r, targetURL)
}

func usersHandler(w http.ResponseWriter, r *http.Request) {
	// Always route users to monolith (not migrated yet)
	forwardRequest(w, r, config.MonolithURL)
}

func paymentsHandler(w http.ResponseWriter, r *http.Request) {
	// Always route payments to monolith (not migrated yet)
	forwardRequest(w, r, config.MonolithURL)
}

func subscriptionsHandler(w http.ResponseWriter, r *http.Request) {
	// Always route subscriptions to monolith (not migrated yet)
	forwardRequest(w, r, config.MonolithURL)
}

func proxyHandler(w http.ResponseWriter, r *http.Request) {
	// Default proxy to monolith for all other routes
	forwardRequest(w, r, config.MonolithURL)
}

func forwardRequest(w http.ResponseWriter, r *http.Request, targetURL string) {
	// Parse target URL
	target, err := url.Parse(targetURL)
	if err != nil {
		http.Error(w, "Invalid target URL", http.StatusInternalServerError)
		return
	}

	// Create reverse proxy
	proxy := httputil.NewSingleHostReverseProxy(target)
	
	// Modify the request to preserve the original path
	originalDirector := proxy.Director
	proxy.Director = func(req *http.Request) {
		originalDirector(req)
		
		// Preserve the original path
		req.URL.Path = r.URL.Path
		req.URL.RawPath = r.URL.RawPath
		
		// Preserve query parameters
		req.URL.RawQuery = r.URL.RawQuery
		
		// Log the proxied request
		log.Printf("Proxying %s %s to %s", req.Method, req.URL.Path, targetURL)
	}

	// Serve the request
	proxy.ServeHTTP(w, r)
}