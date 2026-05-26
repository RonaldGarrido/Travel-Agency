import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  getAllPackages,
  createPackage,
  updatePackage,
  changePackageStatus,
  deletePackage
} from '../../api/packageApi'
import { useAuth } from '../../context/AuthContext'

const emptyForm = {
  name: '',
  destination: '',
  description: '',
  startDate: '',
  endDate: '',
  price: '',
  totalSlots: '',
  includedServices: '',
  conditions: '',
  restrictions: '',
  type: 'NATIONAL'
}

export default function AdminPackagesPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [packages, setPackages] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    // user.roles es array — se usa .includes() para verificar el rol ADMIN
    if (!user || !user.roles?.includes('ADMIN')) {
      navigate('/')
      return
    }
    fetchPackages()
  }, [user, navigate])

  const fetchPackages = async () => {
    try {
      const response = await getAllPackages()
      setPackages(response.data)
    } catch {
      setError('Error al cargar los paquetes')
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleEdit = (pkg) => {
    setEditingId(pkg.id)
    setForm({
      name: pkg.name,
      destination: pkg.destination,
      description: pkg.description,
      startDate: pkg.startDate,
      endDate: pkg.endDate,
      price: pkg.price,
      totalSlots: pkg.totalSlots,
      includedServices: pkg.includedServices || '',
      conditions: pkg.conditions || '',
      restrictions: pkg.restrictions || '',
      type: pkg.type || 'NATIONAL'
    })
    setShowForm(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSaving(true)
    try {
      if (editingId) {
        await updatePackage(editingId, form)
      } else {
        await createPackage(form)
      }
      setShowForm(false)
      setEditingId(null)
      setForm(emptyForm)
      fetchPackages()
    } catch (err) {
      setError(err.response?.data?.message || 'Error al guardar el paquete')
    } finally {
      setSaving(false)
    }
  }

  const handleStatusChange = async (id, status) => {
    try {
      await changePackageStatus(id, status)
      fetchPackages()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al cambiar el estado')
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Eliminar este paquete?')) return
    try {
      await deletePackage(id)
      fetchPackages()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al eliminar el paquete')
    }
  }

  const statusColors = {
    AVAILABLE: { color: '#2d8a2d', bg: '#e8f8e8' },
    SOLD_OUT: { color: '#f0a500', bg: '#fff8e1' },
    NOT_VALID: { color: '#999', bg: '#f5f5f5' },
    CANCELLED: { color: '#e94560', bg: '#fff0f0' }
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <div style={styles.header}>
          <h1 style={styles.title}>Gestión de paquetes</h1>
          <button
            onClick={() => { setShowForm(true); setEditingId(null); setForm(emptyForm) }}
            style={styles.addBtn}
          >
            + Nuevo paquete
          </button>
        </div>

        {error && <div style={styles.error}>{error}</div>}

        {showForm && (
          <div style={styles.formCard}>
            <h3 style={styles.formTitle}>{editingId ? 'Editar paquete' : 'Nuevo paquete'}</h3>
            <form onSubmit={handleSubmit} style={styles.form}>
              <div style={styles.formGrid}>
                <div style={styles.field}>
                  <label style={styles.label}>Nombre</label>
                  <input name="name" value={form.name} onChange={handleChange} required style={styles.input} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Destino</label>
                  <input name="destination" value={form.destination} onChange={handleChange} required style={styles.input} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Fecha inicio</label>
                  <input type="date" name="startDate" value={form.startDate} onChange={handleChange} required style={styles.input} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Fecha término</label>
                  <input type="date" name="endDate" value={form.endDate} onChange={handleChange} required style={styles.input} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Precio</label>
                  <input type="number" name="price" value={form.price} onChange={handleChange} required style={styles.input} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Cupos totales</label>
                  <input type="number" name="totalSlots" value={form.totalSlots} onChange={handleChange} required style={styles.input} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Tipo</label>
                  <select name="type" value={form.type} onChange={handleChange} style={styles.input}>
                    <option value="NATIONAL">Nacional</option>
                    <option value="INTERNATIONAL">Internacional</option>
                  </select>
                </div>
              </div>
              <div style={styles.field}>
                <label style={styles.label}>Descripción</label>
                <textarea name="description" value={form.description} onChange={handleChange} required rows={3} style={{ ...styles.input, resize: 'vertical' }} />
              </div>
              <div style={styles.field}>
                <label style={styles.label}>Servicios incluidos</label>
                <textarea name="includedServices" value={form.includedServices} onChange={handleChange} rows={2} style={{ ...styles.input, resize: 'vertical' }} />
              </div>
              <div style={styles.formGrid}>
                <div style={styles.field}>
                  <label style={styles.label}>Condiciones</label>
                  <textarea name="conditions" value={form.conditions} onChange={handleChange} rows={2} style={{ ...styles.input, resize: 'vertical' }} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Restricciones</label>
                  <textarea name="restrictions" value={form.restrictions} onChange={handleChange} rows={2} style={{ ...styles.input, resize: 'vertical' }} />
                </div>
              </div>
              <div style={styles.formActions}>
                <button type="submit" disabled={saving} style={styles.saveBtn}>
                  {saving ? 'Guardando...' : editingId ? 'Actualizar' : 'Crear'}
                </button>
                <button type="button" onClick={() => { setShowForm(false); setEditingId(null) }} style={styles.cancelFormBtn}>
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}

        {loading ? (
          <p style={styles.center}>Cargando...</p>
        ) : (
          <div style={styles.tableWrap}>
            <table style={styles.table}>
              <thead>
                <tr style={styles.thead}>
                  <th style={styles.th}>Nombre</th>
                  <th style={styles.th}>Destino</th>
                  <th style={styles.th}>Precio</th>
                  <th style={styles.th}>Cupos</th>
                  <th style={styles.th}>Estado</th>
                  <th style={styles.th}>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {packages.map((pkg) => {
                  const sc = statusColors[pkg.status] || statusColors.NOT_VALID
                  return (
                    <tr key={pkg.id} style={styles.tr}>
                      <td style={styles.td}>{pkg.name}</td>
                      <td style={styles.td}>{pkg.destination}</td>
                      <td style={styles.td}>${pkg.price?.toLocaleString('es-CL')}</td>
                      <td style={styles.td}>{pkg.availableSlots}/{pkg.totalSlots}</td>
                      <td style={styles.td}>
                        <span style={{ ...styles.badge, color: sc.color, backgroundColor: sc.bg }}>
                          {pkg.status}
                        </span>
                      </td>
                      <td style={styles.td}>
                        <div style={styles.actions}>
                          <button onClick={() => handleEdit(pkg)} style={styles.editBtn}>Editar</button>
                          <select
                            value={pkg.status}
                            onChange={(e) => handleStatusChange(pkg.id, e.target.value)}
                            style={styles.statusSelect}
                          >
                            <option value="AVAILABLE">Disponible</option>
                            <option value="SOLD_OUT">Agotado</option>
                            <option value="NOT_VALID">No vigente</option>
                            <option value="CANCELLED">Cancelado</option>
                          </select>
                          <button onClick={() => handleDelete(pkg.id)} style={styles.deleteBtn}>Eliminar</button>
                        </div>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

const styles = {
  page: { minHeight: 'calc(100vh - 64px)', backgroundColor: '#f5f5f5', padding: '32px 16px' },
  container: { maxWidth: '1100px', margin: '0 auto' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
  title: { fontSize: '24px', fontWeight: '700', color: '#1a1a2e' },
  addBtn: { padding: '10px 20px', backgroundColor: '#e94560', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px', fontWeight: '500' },
  error: { backgroundColor: '#fff0f0', color: '#e94560', padding: '12px', borderRadius: '8px', marginBottom: '16px', fontSize: '14px' },
  formCard: { backgroundColor: '#fff', borderRadius: '12px', padding: '24px', marginBottom: '24px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' },
  formTitle: { fontSize: '18px', fontWeight: '600', color: '#1a1a2e', marginBottom: '20px' },
  form: { display: 'flex', flexDirection: 'column', gap: '16px' },
  formGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '16px' },
  field: { display: 'flex', flexDirection: 'column', gap: '6px' },
  label: { fontSize: '13px', fontWeight: '500', color: '#333' },
  input: { padding: '10px 12px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px', outline: 'none', width: '100%', boxSizing: 'border-box' },
  formActions: { display: 'flex', gap: '12px' },
  saveBtn: { padding: '10px 24px', backgroundColor: '#e94560', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px', fontWeight: '500' },
  cancelFormBtn: { padding: '10px 24px', backgroundColor: 'transparent', color: '#666', border: '1px solid #ddd', borderRadius: '8px', cursor: 'pointer', fontSize: '14px' },
  tableWrap: { overflowX: 'auto' },
  table: { width: '100%', borderCollapse: 'collapse', backgroundColor: '#fff', borderRadius: '12px', overflow: 'hidden', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' },
  thead: { backgroundColor: '#1a1a2e' },
  th: { padding: '14px 16px', textAlign: 'left', fontSize: '13px', fontWeight: '500', color: '#fff' },
  tr: { borderBottom: '1px solid #f0f0f0' },
  td: { padding: '14px 16px', fontSize: '14px', color: '#333' },
  badge: { padding: '4px 10px', borderRadius: '20px', fontSize: '12px', fontWeight: '500' },
  actions: { display: 'flex', gap: '8px', alignItems: 'center' },
  editBtn: { padding: '6px 12px', backgroundColor: '#1a1a2e', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' },
  statusSelect: { padding: '6px 8px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '12px', cursor: 'pointer' },
  deleteBtn: { padding: '6px 12px', backgroundColor: 'transparent', color: '#e94560', border: '1px solid #e94560', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' },
  center: { textAlign: 'center', color: '#999', padding: '40px' }
}
