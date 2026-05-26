import React, { useState, useEffect } from 'react';

const styles = {
  page: {
    minHeight: 'calc(100vh - 64px)',
    backgroundColor: '#f5f5f5',
    padding: '32px 16px'
  },
  container: {
    maxWidth: '1200px',
    margin: '0 auto'
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '24px'
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
    gap: '20px'
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    overflow: 'hidden',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
    transition: 'transform 0.3s ease, box-shadow 0.3s ease',
    cursor: 'pointer'
  },
  cardHover: {
    transform: 'translateY(-4px)',
    boxShadow: '0 4px 16px rgba(0,0,0,0.12)'
  },
  cardImage: {
    width: '100%',
    height: '200px',
    objectFit: 'cover',
    backgroundColor: '#e0e0e0'
  },
  cardContent: {
    padding: '16px'
  },
  packageName: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '8px'
  },
  description: {
    fontSize: '14px',
    color: '#666',
    marginBottom: '12px',
    lineHeight: '1.5'
  },
  meta: {
    fontSize: '13px',
    color: '#999',
    marginBottom: '12px'
  },
  priceSection: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '16px',
    paddingTop: '16px',
    borderTop: '1px solid #eee'
  },
  price: {
    fontSize: '20px',
    fontWeight: '700',
    color: '#e94560'
  },
  bookBtn: {
    padding: '10px 20px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500',
    transition: 'background-color 0.3s ease'
  },
  loading: {
    textAlign: 'center',
    padding: '60px 20px',
    color: '#999'
  },
  error: {
    textAlign: 'center',
    padding: '60px 20px',
    color: '#e94560'
  }
};

export default function Catalog() {
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [hoveredCard, setHoveredCard] = useState(null);

  useEffect(() => {
    // Simulación de carga de paquetes
    setTimeout(() => {
      setPackages([
        { id: 1, name: 'Playa Caribeña', duration: '7 días', price: 1200, description: 'Disfruta del paraíso' },
        { id: 2, name: 'Montaña Nevada', duration: '5 días', price: 950, description: 'Aventura en la naturaleza' },
        { id: 3, name: 'Ciudad Histórica', duration: '4 días', price: 800, description: 'Conoce la historia' }
      ]);
      setLoading(false);
    }, 500);
  }, []);

  if (loading) {
    return <div style={styles.loading}>Cargando catálogo...</div>;
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>Catálogo de Viajes</h1>
        <div style={styles.grid}>
          {packages.map(pkg => (
            <div
              key={pkg.id}
              style={{
                ...styles.card,
                ...(hoveredCard === pkg.id ? styles.cardHover : {})
              }}
              onMouseEnter={() => setHoveredCard(pkg.id)}
              onMouseLeave={() => setHoveredCard(null)}
            >
              <div style={styles.cardImage}></div>
              <div style={styles.cardContent}>
                <h3 style={styles.packageName}>{pkg.name}</h3>
                <p style={styles.description}>{pkg.description}</p>
                <p style={styles.meta}>{pkg.duration}</p>
                <div style={styles.priceSection}>
                  <span style={styles.price}>${pkg.price}</span>
                  <button style={styles.bookBtn}>Reservar</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
