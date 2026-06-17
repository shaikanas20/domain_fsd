import React, { createContext, useState, useEffect } from 'react';
import API from '../services/api';

export const LocationContext = createContext();

export const LocationProvider = ({ children }) => {
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [locations, setLocations] = useState([]);

  useEffect(() => {
    const fetchLocations = async () => {
      try {
        const response = await API.get('/locations');
        const locs = response.data.data;
        setLocations(locs);
        
        // Restore from storage or select first
        const storedLoc = localStorage.getItem('selectedLocation');
        if (storedLoc) {
          setSelectedLocation(JSON.parse(storedLoc));
        } else if (locs.length > 0) {
          setSelectedLocation(locs[0]);
          localStorage.setItem('selectedLocation', JSON.stringify(locs[0]));
        }
      } catch (error) {
        console.error('Failed to load locations', error);
      }
    };
    fetchLocations();
  }, []);

  const selectLocation = (location) => {
    setSelectedLocation(location);
    localStorage.setItem('selectedLocation', JSON.stringify(location));
  };

  return (
    <LocationContext.Provider value={{ selectedLocation, locations, selectLocation }}>
      {children}
    </LocationContext.Provider>
  );
};
