const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const app = express();

// Middleware

app.use(express.json());

// 🔗 Connexion MongoDB
mongoose.connect('mongodb://127.0.0.1:27017/clubDB')
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log(err));


// 🧱 Schema Club    
const clubSchema = new mongoose.Schema({
  nomClub: { type: String, required: true },
  debutTravail: { type: String, required: true },
  finTravail: { type: String, required: true },
  nombreTerrains: { type: Number, required: true },
  prix: { type: Number, required: true }
});

const Club = mongoose.model('Club', clubSchema);


// 🧱 Schema Reservation
const reservationSchema = new mongoose.Schema({
  userId: { type: String, required: true },
  clubId: { type: String, required: true },
  terrainName: { type: String, required: true }, // ex: dd-T1
  date: { type: String, required: true },
  startTime: { type: String, required: true },
  endTime: { type: String, required: true }
});

const Reservation = mongoose.model('Reservation', reservationSchema);


// 🚀 CREATE CLUB
app.post('/clubs', async (req, res) => {
  try {
    const club = new Club(req.body);
    await club.save();
    res.status(201).json(club);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});


// 📥 GET ALL CLUBS
app.get('/clubs', async (req, res) => {
  const clubs = await Club.find();
  res.json(clubs);
});


// 📥 GET ONE CLUB
app.get('/clubs/:id', async (req, res) => {
  try {
    const club = await Club.findById(req.params.id);
    if (!club) return res.status(404).json({ message: "Not found" });
    res.json(club);
  } catch (err) {
    res.status(400).json({ error: "Invalid ID" });
  }
});


// ❌ DELETE CLUB
app.delete('/clubs/:id', async (req, res) => {
  try {
    await Club.findByIdAndDelete(req.params.id);
    res.json({ message: "Deleted" });
  } catch (err) {
    res.status(400).json({ error: "Invalid ID" });
  }
});


// 🔥 ====== LOGIQUE RESERVATION ======

// ⏰ Générer créneaux 1h30
function generateSlots(start, end) {
  const slots = [];
  let current = parseTime(start);
  const endTime = parseTime(end);

  while (current + 90 <= endTime) {
    let next = current + 90;
    slots.push({
      start: formatTime(current),
      end: formatTime(next)
    });
    current = next;
  }

  return slots;
}

function parseTime(t) {
  const [h, m] = t.split(':').map(Number);
  return h * 60 + m;
}

function formatTime(m) {
  const h = Math.floor(m / 60);
  const min = m % 60;
  return `${h.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')}`;
}


// 📥 GET créneaux disponibles
app.get('/reservations/available', async (req, res) => {
  const { clubId, terrainName, date } = req.query;

  try {
    const club = await Club.findById(clubId);
    if (!club) return res.status(404).json({ message: "Club not found" });

    const allSlots = generateSlots(club.debutTravail, club.finTravail);

    const reservations = await Reservation.find({
      terrainName,
      date
    });

    const booked = reservations.map(r => r.startTime);

    const available = allSlots.filter(slot => !booked.includes(slot.start));

    res.json(available);

  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});


// 🚀 CREATE RESERVATION
app.post('/reservations', async (req, res) => {
  try {
    const { userId, clubId, terrainName, date, startTime, endTime } = req.body;

    // 🔴 vérifier doublon
    const exist = await Reservation.findOne({
      terrainName,
      date,
      startTime
    });

    if (exist) {
      return res.status(400).json({ message: "Créneau déjà réservé" });
    }

    const reservation = new Reservation({
      userId,
      clubId,
      terrainName,
      date,
      startTime,
      endTime
    });

    await reservation.save();

    res.status(201).json(reservation);

  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

app.get('/reservations', async (req, res) => {
  try {
    const reservations = await Reservation.find();
    res.json(reservations);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/reservations', async (req, res) => {
  try {
    const filter = {};

    if (req.query.clubId) {
      filter.clubId = req.query.clubId;
    }

    const reservations = await Reservation.find(filter);
    res.json(reservations);

  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/reservations/my', async (req, res) => {
  try {
    const { userId } = req.query;

    if (!userId) {
      return res.status(400).json({ message: "userId required" });
    }

    const reservations = await Reservation.find({ userId });

    res.json(reservations);

  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/reservations/:id', async (req, res) => {
  try {
    await Reservation.findByIdAndDelete(req.params.id);
    res.json({ message: "Reservation deleted" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});




const Eureka = require('eureka-js-client').Eureka;

const client = new Eureka({
  instance: {
    app: 'RESERVATION-SERVICE',
    instanceId: 'reservation-node-1',
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    port: {
      '$': 3000,
      '@enabled': true,
    },
    vipAddress: 'RESERVATION-SERVICE',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/', 
  },
});

client.start();


// ▶️ SERVER
app.listen(3000, () => {
  console.log('Server running on http://localhost:3000');
});
