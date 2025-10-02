export const sampleCategories = [
  {
    id: 1,
    name: 'Civil Litigation',
    description: 'Disputes between private parties including tort and contract matters.',
    createdAt: '2025-01-18T09:15:00',
    updatedAt: '2025-07-15T11:30:00',
    owner: 'Alex Morgan'
  },
  {
    id: 2,
    name: 'Criminal Defense',
    description: 'Matters involving alleged criminal conduct and government prosecution.',
    createdAt: '2025-02-05T13:40:00',
    updatedAt: '2025-08-10T08:25:00',
    owner: 'Priya Nair'
  },
  {
    id: 3,
    name: 'Municipal Law',
    description: 'Land use, zoning, and administrative issues for local governments.',
    createdAt: '2025-03-12T10:05:00',
    updatedAt: '2025-08-28T15:55:00',
    owner: 'Jordan Blake'
  },
  {
    id: 4,
    name: 'Corporate Compliance',
    description: 'Regulatory adherence, audits, and corporate governance matters.',
    createdAt: '2025-04-01T09:50:00',
    updatedAt: '2025-09-01T14:05:00',
    owner: 'Taylor Kim'
  },
  {
    id: 5,
    name: 'Family Law',
    description: 'Custody, adoption, and estate matters for families.',
    createdAt: '2025-05-09T08:20:00',
    updatedAt: '2025-09-22T17:00:00',
    owner: 'Jamie Patel'
  }
]

export const sampleCases = [
  {
    id: 101,
    caseNum: 'CASE-2025-072',
    title: 'Anderson vs City Council',
    status: 'active',
    categoryIds: [1, 3],
    categories: ['Civil Litigation', 'Municipal Law'],
    hearingAt: '2025-10-03T10:00:00',
    createdAt: '2025-07-12T09:30:00',
    updatedAt: '2025-09-04T14:40:00',
    lawyerId: 11,
    description: 'Residents challenge a zoning variance issued by the council.'
  },
  {
    id: 102,
    caseNum: 'CASE-2025-108',
    title: 'State vs Porter Manufacturing',
    status: 'active',
    categoryIds: [2, 4],
    categories: ['Criminal Defense', 'Corporate Compliance'],
    hearingAt: '2025-10-06T14:30:00',
    createdAt: '2025-06-20T11:05:00',
    updatedAt: '2025-09-28T16:05:00',
    lawyerId: 23,
    description: 'Environmental violations related to hazardous waste disposal.'
  },
  {
    id: 103,
    caseNum: 'CASE-2025-211',
    title: 'Lopez vs Evergreen Homes',
    status: 'draft',
    categoryIds: [1, 5],
    categories: ['Civil Litigation', 'Family Law'],
    hearingAt: '2025-10-09T09:15:00',
    createdAt: '2025-05-17T08:15:00',
    updatedAt: '2025-08-25T10:20:00',
    lawyerId: 18,
    description: 'Construction defects claim for a single-family residence.'
  },
  {
    id: 104,
    caseNum: 'CASE-2025-317',
    title: 'In re Estate of Carter',
    status: 'closed',
    categoryIds: [5],
    categories: ['Family Law'],
    hearingAt: '2025-10-12T11:00:00',
    createdAt: '2025-02-05T13:00:00',
    updatedAt: '2025-08-18T09:40:00',
    lawyerId: 9,
    description: 'Probate proceedings for the Carter family estate.'
  },
  {
    id: 105,
    caseNum: 'CASE-2025-455',
    title: 'City of Riverton vs Blake Logistics',
    status: 'active',
    categoryIds: [1, 4],
    categories: ['Civil Litigation', 'Corporate Compliance'],
    hearingAt: '2025-10-15T13:45:00',
    createdAt: '2025-06-02T10:10:00',
    updatedAt: '2025-09-28T08:55:00',
    lawyerId: 31,
    description: 'Breach of contract claim involving municipal hauling services.'
  },
  {
    id: 106,
    caseNum: 'CASE-2025-512',
    title: 'Hernandez vs Central Medical',
    status: 'active',
    categoryIds: [1],
    categories: ['Civil Litigation'],
    hearingAt: '2025-10-18T10:30:00',
    createdAt: '2025-06-22T12:45:00',
    updatedAt: '2025-09-30T09:25:00',
    lawyerId: 14,
    description: 'Medical negligence claim related to post-operative care.'
  }
]

export const sampleEvidence = [
  {
    id: 401,
    caseId: 101,
    title: 'Site Inspection Report.pdf',
    description: 'Summary of zoning compliance visit.',
    fileUrl: 'https://example.com/docs/site-inspection-report.pdf',
    uploadedBy: 11,
    createdAt: '2025-08-15T11:20:00',
    sizeBytes: 234000
  },
  {
    id: 402,
    caseId: 101,
    title: 'Public Feedback Summary.pdf',
    description: 'Community comments collected during planning session.',
    fileUrl: 'https://example.com/docs/public-feedback.pdf',
    uploadedBy: 17,
    createdAt: '2025-08-22T16:45:00',
    sizeBytes: 178000
  },
  {
    id: 403,
    caseId: 102,
    title: 'Environmental Compliance Audit.xlsx',
    description: 'Audit workbook for plant processes.',
    fileUrl: 'https://example.com/docs/environmental-audit.xlsx',
    uploadedBy: 23,
    createdAt: '2025-08-28T09:05:00',
    sizeBytes: 512000
  },
  {
    id: 404,
    caseId: 105,
    title: 'Contract Exhibit A.pdf',
    description: 'Master hauling agreement.',
    fileUrl: 'https://example.com/docs/contract-exhibit-a.pdf',
    uploadedBy: 31,
    createdAt: '2025-09-12T13:10:00',
    sizeBytes: 295000
  },
  {
    id: 405,
    caseId: 106,
    title: 'Post-Operative Chart Notes.txt',
    description: 'Timeline of procedures following surgery.',
    fileUrl: 'https://example.com/docs/chart-notes.txt',
    uploadedBy: 14,
    createdAt: '2025-09-18T08:32:00',
    sizeBytes: 89000
  }
]

export const sampleHearings = [
  {
    id: 301,
    caseId: 101,
    caseNumber: 'CASE-2025-072',
    title: 'Anderson vs City Council',
    hearingAt: '2025-10-03T10:00:00'
  },
  {
    id: 302,
    caseId: 102,
    caseNumber: 'CASE-2025-108',
    title: 'State vs Porter Manufacturing',
    hearingAt: '2025-10-06T14:30:00'
  },
  {
    id: 303,
    caseId: 103,
    caseNumber: 'CASE-2025-211',
    title: 'Lopez vs Evergreen Homes',
    hearingAt: '2025-10-09T09:15:00'
  },
  {
    id: 304,
    caseId: 104,
    caseNumber: 'CASE-2025-317',
    title: 'In re Estate of Carter',
    hearingAt: '2025-10-12T11:00:00'
  },
  {
    id: 305,
    caseId: 105,
    caseNumber: 'CASE-2025-455',
    title: 'City of Riverton vs Blake Logistics',
    hearingAt: '2025-10-15T13:45:00'
  },
  {
    id: 306,
    caseId: 106,
    caseNumber: 'CASE-2025-512',
    title: 'Hernandez vs Central Medical',
    hearingAt: '2025-10-18T10:30:00'
  }
]
