import React, { useState, useEffect } from "react";
import { Card, Button, Table, message, Row, Col, Divider, Modal } from "antd";
import { DeleteOutlined, PlusOutlined } from "@ant-design/icons";
import { getSeason, deleteSeason, addSeason } from "@/api/season";
import TypingCard from "@/components/TypingCard";
import AddSeasonForm from "./forms/add-season-form";

const Season = () => {
  const [season, setSeason] = useState([]);
  const [addVisible, setAddVisible] = useState(false);
  const [addLoading, setAddLoading] = useState(false);

  const fetchSeason = async () => {
    try {
      const result = await getSeason();
      if (result.data.statusCode === 200) {
        setSeason(result.data.content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data kelas ajaran");
    }
  };

  useEffect(() => {
    fetchSeason();
  }, []);

  const handleDeleteSeason = async (row) => {
    const id = row.idSeason;
    try {
      await deleteSeason({ id });
      message.success("Berhasil dihapus");
      fetchSeason();
    } catch (error) {
      message.error("Gagal menghapus kelas ajaran");
    }
  };

  const handleAddSeasonOk = async (values) => {
    setAddLoading(true);
    try {
      await addSeason(values);
      message.success("Berhasil menambahkan kelas ajaran");
      setAddVisible(false);
      fetchSeason();
    } catch (error) {
      message.error("Gagal menambahkan kelas ajaran");
    } finally {
      setAddLoading(false);
    }
  };

  const columns = [
    {
      title: "Tahun Ajaran",
      key: "tahunAjaran",
      render: (_, row) => row.tahunAjaran?.tahunAjaran || "-",
    },
    {
      title: "Semester",
      key: "semester",
      render: (_, row) => row.semester?.namaSemester || "-",
    },
    {
      title: "Program Studi",
      key: "studyProgram",
      render: (_, row) => {
        const studyProgram = row.studyProgram || row.study_program;
        return studyProgram?.name || "-";
      },
    },
    {
      title: "Kelas",
      key: "kelas",
      render: (_, row) => row.kelas?.namaKelas || "-",
    },
    {
      title: "Dosen",
      key: "lecture",
      render: (_, row) => row.lecture?.name || "-",
    },
    {
      title: "Mahasiswa",
      key: "student",
      render: (_, row) => (Array.isArray(row.student) ? row.student.length : 0),
    },
    {
      title: "Mata Kuliah",
      key: "subject",
      render: (_, row) => (Array.isArray(row.subject) ? row.subject.length : 0),
    },
    {
      title: "Aksi",
      key: "action",
      render: (_, row) => (
        <Button
          danger
          shape="circle"
          icon={<DeleteOutlined />}
          onClick={() =>
            Modal.confirm({
              title: "Hapus Kelas Ajaran",
              content: "Data kelas ajaran ini akan dihapus permanen.",
              okText: "Hapus",
              okType: "danger",
              cancelText: "Batal",
              onOk: () => handleDeleteSeason(row),
            })
          }
        />
      ),
    },
  ];

  const cardContent =
    "Di sini, Anda dapat mengelola kelas ajaran sebagai penghubung prodi, kelas, tahun ajaran, semester, dosen, mahasiswa, dan mata kuliah aktif.";

  return (
    <div className="app-container">
      <TypingCard title="Manajemen Kelas Ajaran" source={cardContent} />
      <br />
      <Card
        title={
          <Row justify="start" style={{ paddingLeft: 9 }}>
            <Col>
              <Button type="primary" icon={<PlusOutlined />} onClick={() => setAddVisible(true)}>
                Tambahkan Kelas Ajaran
              </Button>
            </Col>
          </Row>
        }
      >
        <Table rowKey="idSeason" dataSource={season} columns={columns} pagination={{ pageSize: 10 }} />
      </Card>

      <AddSeasonForm
        visible={addVisible}
        confirmLoading={addLoading}
        onCancel={() => setAddVisible(false)}
        onOk={handleAddSeasonOk}
      />
    </div>
  );
};

export default Season;
