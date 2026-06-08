/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import { Card, Button, Table, message, Divider } from "antd";
import {
  getStudents,
  deleteStudent,
  editStudent,
  addStudent,
} from "@/api/student";
import TypingCard from "@/components/TypingCard";
import EditStudentForm from "./forms/edit-student-form";
import AddStudentForm from "./forms/add-student-form";

const { Column } = Table;

const Student = () => {
  const [students, setStudents] = useState([]);
  const [modalState, setModalState] = useState({
    editVisible: false,
    editLoading: false,
    addVisible: false,
    addLoading: false,
  });
  const [currentRowData, setCurrentRowData] = useState({});

  const fetchData = async () => {
    try {
      const studentsRes = await getStudents();

      if (studentsRes.data.statusCode === 200) {
        setStudents(studentsRes.data.content || []);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
      message.error("Gagal memuat data mahasiswa");
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleEditStudent = (row) => {
    setCurrentRowData({ ...row });
    setModalState((prev) => ({ ...prev, editVisible: true }));
  };

  const handleDeleteStudent = async (row) => {
    if (row.id === "admin") {
      message.error("Tidak dapat menghapus Admin!");
      return;
    }

    try {
      await deleteStudent({ id: row.id });
      message.success("Berhasil dihapus");
      fetchData();
    } catch (error) {
      message.error("Gagal menghapus");
    }
  };

  const handleEditStudentOk = async (values) => {
    setModalState((prev) => ({ ...prev, editLoading: true }));
    try {
      await editStudent(values, currentRowData.id);
      setModalState((prev) => ({
        ...prev,
        editVisible: false,
        editLoading: false,
      }));
      message.success("Berhasil diperbarui!");
      fetchData();
    } catch (error) {
      console.error("Gagal memperbarui mahasiswa:", error.response?.data || error);
      message.error(error.response?.data?.message || "Gagal memperbarui");
      setModalState((prev) => ({ ...prev, editLoading: false }));
    }
  };

  const handleCancel = () => {
    setModalState((prev) => ({
      ...prev,
      editVisible: false,
      addVisible: false,
    }));
  };

  const handleAddStudent = () => {
    setModalState((prev) => ({ ...prev, addVisible: true }));
  };

  const handleAddStudentOk = async (values) => {
    setModalState((prev) => ({ ...prev, addLoading: true }));
    try {
      console.log("Payload tambah mahasiswa:", values);
      await addStudent(values);
      setModalState((prev) => ({
        ...prev,
        addVisible: false,
        addLoading: false,
      }));
      message.success("Berhasil ditambahkan!");
      fetchData();
    } catch (error) {
      console.error("Gagal menambahkan mahasiswa:", error.response?.data || error);
      message.error(error.response?.data?.message || "Gagal menambahkan, coba lagi!");
      setModalState((prev) => ({ ...prev, addLoading: false }));
    }
  };

  const cardContent = `Di sini, Anda dapat mengelola data mahasiswa berdasarkan program studi, kelas, tahun ajaran, agama, dan biodata mahasiswa.`;

  return (
    <div className="app-container">
      <TypingCard title="Manajemen Mahasiswa" source={cardContent} />
      <br />
      <Card
        title={
          <Button type="primary" onClick={handleAddStudent}>
            Tambahkan Mahasiswa
          </Button>
        }
      >
        <Table variant rowKey="id" dataSource={students} pagination={false}>
          <Column title="NISN" dataIndex="nisn" key="nisn" align="center" />
          <Column title="User Login" dataIndex="user_id" key="user_id" align="center" />
          <Column title="Nama" dataIndex="name" key="name" align="center" />
          <Column title="Gender" dataIndex="gender" key="gender" align="center" />
          <Column title="Telepon" dataIndex="phone" key="phone" align="center" />
          <Column
            title="Program Studi"
            key="studyProgram"
            align="center"
            render={(_, row) => row.studyProgram?.name || row.study_program?.name || "-"}
          />
          <Column
            title="Agama"
            key="religion"
            align="center"
            render={(_, row) => row.religion?.name || "-"}
          />
          <Column
            title="Kelas"
            key="kelas"
            align="center"
            render={(_, row) => row.kelas?.namaKelas || "-"}
          />
          <Column
            title="Tahun Ajaran"
            key="tahunAjaran"
            align="center"
            render={(_, row) => row.tahunAjaran?.tahunAjaran || "-"}
          />
          <Column
            title="Operasi"
            key="action"
            width={195}
            align="center"
            render={(_, row) => (
              <span>
                <Button
                  type="primary"
                  shape="circle"
                  icon="edit"
                  title="mengedit"
                  onClick={() => handleEditStudent(row)}
                />
                <Divider type="vertical" />
                <Button
                  type="primary"
                  shape="circle"
                  icon="delete"
                  title="menghapus"
                  onClick={() => handleDeleteStudent(row)}
                />
              </span>
            )}
          />
        </Table>
      </Card>

      <EditStudentForm
        currentRowData={currentRowData}
        visible={modalState.editVisible}
        confirmLoading={modalState.editLoading}
        onCancel={handleCancel}
        onOk={handleEditStudentOk}
      />

      <AddStudentForm
        visible={modalState.addVisible}
        confirmLoading={modalState.addLoading}
        onCancel={handleCancel}
        onOk={handleAddStudentOk}
      />
    </div>
  );
};

export default Student;
