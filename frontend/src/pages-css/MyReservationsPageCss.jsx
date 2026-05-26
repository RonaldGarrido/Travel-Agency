const styles = {
  page: {
    minHeight: 'calc(100vh - 64px)',
    backgroundColor: '#f5f5f5',
    padding: '32px 16px'
  },
  container: {
    maxWidth: '800px',
    margin: '0 auto'
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '24px'
  },
  list: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px'
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  cardTop: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
    gap: '12px'
  },
  packageName: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '4px'
  },
  meta: {
    fontSize: '14px',
    color: '#666'
  },
  date: {
    fontSize: '13px',
    color: '#999',
    marginTop: '4px'
  },
  right: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '8px'
  },
  statusBadge: {
    padding: '4px 12px',
    borderRadius: '20px',
    fontSize: '13px',
    fontWeight: '500'
  },
  amount: {
    fontSize: '20px',
    fontWeight: '700',
    color: '#e94560'
  },
  discount: {
    fontSize: '12px',
    color: '#2d8a2d',
    fontStyle: 'italic',
    marginTop: '8px'
  },
  cardActions: {
    display: 'flex',
    gap: '12px',
    marginTop: '16px'
  },
  payBtn: {
    padding: '10px 20px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500'
  },
  cancelBtn: {
    padding: '10px 20px',
    backgroundColor: 'transparent',
    color: '#999',
    border: '1px solid #ddd',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px'
  },
  empty: {
    textAlign: 'center',
    padding: '60px',
    color: '#999'
  },
  exploreBtn: {
    marginTop: '16px',
    padding: '12px 24px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500'
  },
  error: {
    color: '#e94560',
    textAlign: 'center'
  },
  center: {
    textAlign: 'center',
    color: '#999'
  }
}