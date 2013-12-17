/*
 * SHOW ALERTROADS IN LEG MAP (planning)
 * "alertroads_in_planning" : true,
 * "alertroads_in_planning_agencyid" : "COMUNE_DI_ROVERETO",
 * 
 * SMART CHECK OPTIONS
 * 1 bus trento timetable
 * 2 bus rovereto timetable
 * 3 suburban timetable
 * 4 train timetable
 * 5 parking trento
 * 6 parking rovereto
 * 7 alerts rovereto
 * 
 * SUBURBAN ZONES
 * 1 Val di Fiemme, Val di Fassa, Val di Cembra
 * 2 Val Rendena, Giudicarie
 * 3 Rovereto, Lavarone, Destra Adige, Riva del Garda
 * 4 Valsugana
 * 5 Valle di Primiero
 * 6 Val di Non, Val di Sole
 * 
 * BROADCAST NOTIFICATIONS OPTIONS
 * 1 bus trento delay
 * 2 bus rovereto delay
 * 3 bus suburban delay
 * 4 train delay
 * 5 accident
 * 6 road works
 * 7 strike
 * 8 traffic jam
 * 9 diversion
 * 
 * COORDINATES
 * Trento 46.069672, 11.121270
 * Rovereto 45.890919, 11.040184
 * 
 *     "suburban_zones" : [
 *        3
 *   ],
 */

{
   "app_token":"vivitrento",
   "smartcheck_options":[
      1,
      4,
      5
   ],
   "broadcast_notifications_options":[
      1,
      4
   ],
   "center_map":[
      46.069672,
      11.121270
   ],
   "zoom_map":15
}